package prflow.spring_backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GitHubAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAuthService.class);

    @Value("${GITHUB_APP_ID:}")
    private String appId;

    @Value("${GITHUB_APP_PRIVATE_KEY:}")
    private String privateKeyPem;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Map<Long, CachedToken> tokenCache = new ConcurrentHashMap<>();

    private record CachedToken(String token, Instant expiresAt) {}

    /**
     * Gets or fetches a cached installation access token for the given installation ID.
     */
    public String getInstallationToken(Long installationId) {
        CachedToken cached = tokenCache.get(installationId);
        if (cached != null && Instant.now().plusSeconds(60).isBefore(cached.expiresAt())) {
            logger.info("Using cached installation token for installationId={}", installationId);
            return cached.token();
        }

        try {
            logger.info("Fetching new installation token for installationId={}", installationId);
            String appJwt = generateAppJwt();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/app/installations/" + installationId + "/access_tokens"))
                .header("Authorization", "Bearer " + appJwt)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "prflow-spring-api")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                logger.error("GitHub installation token request failed status={} body={}", response.statusCode(), response.body());
                throw new RuntimeException("Failed to fetch installation token from GitHub: " + response.statusCode() + " - " + response.body());
            }

            JsonNode node = objectMapper.readTree(response.body());
            String token = node.get("token").asText();
            String expiresAtStr = node.get("expires_at").asText();
            Instant expiresAt = Instant.parse(expiresAtStr);

            tokenCache.put(installationId, new CachedToken(token, expiresAt));
            logger.info("Successfully cached installation token for installationId={} expiring at={}", installationId, expiresAt);
            return token;
        } catch (Exception e) {
            logger.error("Error obtaining installation token for installationId={}", installationId, e);
            throw new RuntimeException("Failed to authenticate installation ID " + installationId, e);
        }
    }

    /**
     * Generates a GitHub App JWT (RS256, 10 min expiration window).
     */
    public String generateAppJwt() throws Exception {
        long now = System.currentTimeMillis() / 1000L;
        long iat = now - 60; // 60 seconds clock drift buffer
        long exp = now + 540; // 9 minutes expiration

        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = String.format("{\"iat\":%d,\"exp\":%d,\"iss\":\"%s\"}", iat, exp, appId.trim());

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        String message = encodedHeader + "." + encodedPayload;

        PrivateKey privateKey = loadPrivateKey(privateKeyPem);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signed = signature.sign();

        String encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signed);
        return message + "." + encodedSignature;
    }

    /**
     * Parses both PKCS#1 and PKCS#8 RSA Private Keys.
     */
    public PrivateKey loadPrivateKey(String pem) throws Exception {
        if (pem == null || pem.trim().isEmpty()) {
            throw new IllegalArgumentException("GitHub App Private Key is missing or empty.");
        }

        // Clean any escaped newlines if passed from shell environments
        String cleanPem = pem.replace("\\n", "\n").trim();

        if (cleanPem.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            // Parse PKCS#1
            String base64 = cleanPem
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(base64);
            return parsePKCS1Der(der);
        } else {
            // Parse PKCS#8
            String base64 = cleanPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(base64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(der);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        }
    }

    /**
     * Internal ASN.1 DER parser for PKCS#1 formatted RSA Private Keys.
     */
    private PrivateKey parsePKCS1Der(byte[] der) throws Exception {
        int index = 0;
        if (der[index++] != 0x30) throw new IOException("Invalid DER sequence");
        
        // Skip sequence length
        int sequenceLength = getDerLength(der, index);
        index += getDerLengthBytes(der, index);

        // Version
        BigInteger version = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // Modulus
        BigInteger modulus = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // PublicExponent
        BigInteger publicExponent = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // PrivateExponent
        BigInteger privateExponent = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // Prime1
        BigInteger prime1 = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // Prime2
        BigInteger prime2 = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // Exponent1
        BigInteger exponent1 = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // Exponent2
        BigInteger exponent2 = readDerInteger(der, index);
        index += getDerIntegerBytes(der, index);

        // Coefficient
        BigInteger coefficient = readDerInteger(der, index);

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
            modulus, publicExponent, privateExponent, prime1, prime2, exponent1, exponent2, coefficient
        );
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private int getDerLength(byte[] der, int index) {
        int val = der[index] & 0xFF;
        if ((val & 0x80) == 0) return val;
        int count = val & 0x7F;
        int len = 0;
        for (int i = 1; i <= count; i++) {
            len = (len << 8) | (der[index + i] & 0xFF);
        }
        return len;
    }

    private int getDerLengthBytes(byte[] der, int index) {
        int val = der[index] & 0xFF;
        if ((val & 0x80) == 0) return 1;
        return 1 + (val & 0x7F);
    }

    private BigInteger readDerInteger(byte[] der, int index) throws IOException {
        if (der[index++] != 0x02) throw new IOException("Invalid DER integer identifier");
        int len = getDerLength(der, index);
        int lenBytes = getDerLengthBytes(der, index);
        index += lenBytes;
        byte[] val = new byte[len];
        System.arraycopy(der, index, val, 0, len);
        return new BigInteger(val);
    }

    private int getDerIntegerBytes(byte[] der, int index) {
        int len = getDerLength(der, index + 1);
        int lenBytes = getDerLengthBytes(der, index + 1);
        return 1 + lenBytes + len;
    }

    // For testing and clearing cache
    public void clearCache() {
        tokenCache.clear();
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setPrivateKeyPem(String privateKeyPem) {
        this.privateKeyPem = privateKeyPem;
    }
}
