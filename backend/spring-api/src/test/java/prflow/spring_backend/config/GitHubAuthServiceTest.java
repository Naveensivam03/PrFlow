package prflow.spring_backend.config;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GitHubAuthServiceTest {

    private GitHubAuthService authService;

    @BeforeEach
    void setUp() {
        authService = new GitHubAuthService();
        authService.setAppId("3758349");
    }

    @Test
    void shouldLoadPkcs8PrivateKeyAndGenerateJwt() throws Exception {
        // 1. Generate standard RSA keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // 2. Encode to PKCS#8 PEM
        String pem = "-----BEGIN PRIVATE KEY-----\n" +
            Base64.getMimeEncoder().encodeToString(kp.getPrivate().getEncoded()) +
            "\n-----END PRIVATE KEY-----";

        authService.setPrivateKeyPem(pem);

        // 3. Load private key
        PrivateKey loadedKey = authService.loadPrivateKey(pem);
        assertNotNull(loadedKey);
        assertEquals("RSA", loadedKey.getAlgorithm());

        // 4. Generate JWT
        String jwt = authService.generateAppJwt();
        assertNotNull(jwt);

        // JWT has exactly 3 parts separated by dots
        String[] parts = jwt.split("\\.");
        assertEquals(3, parts.length);

        // Check header decoding
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        assertTrue(headerJson.contains("\"alg\":\"RS256\""));
        assertTrue(headerJson.contains("\"typ\":\"JWT\""));

        // Check payload decoding
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        assertTrue(payloadJson.contains("\"iss\":\"3758349\""));
        assertTrue(payloadJson.contains("\"iat\":"));
        assertTrue(payloadJson.contains("\"exp\":"));
    }

    @Test
    void shouldLoadPkcs1PrivateKey() throws Exception {
        // A hardcoded valid standard PKCS#1 dummy RSA key to test the ASN.1 parser robustness
        String pkcs1Pem = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpgIBAAKCAQEAxwjWc2S9LouTdJFAlIljoa/xSQzYW48vY1DaQWCrH6ZbzzyY\n" +
            "XuFlJF5xj0BTwNJYKEbV/pNyAvgTmOSaxd4niJfJjsTMZH0nzJlOXp6ayPQH3oIo\n" +
            "KAtNvguKCvcviRxl8eYJhYUFjikQl38iayW2f8MHGlAVPJkkhRgALmLyAh6pwZSg\n" +
            "XRMnupYpGV6kmKyH7c3j5+nmm38Gal1GO2c+cJQ0O9GQaR06mu05tnrsUm1Gb8IL\n" +
            "5zQN/HaOWtAZ/poYXJGqmjbGmcEvsodlim3c6d2IcetAB+6UvYNunA/l8leZWHY5\n" +
            "pX8sNaS5xkuPawFhJabmjIDbcMAIR4hZR66/fQIDAQABAoIBAQCuYurqjfBHsNlQ\n" +
            "f9eyjTHzP6HL3GpQxoLBpNyl/ZnRkEPOroveTeP9WpZq6fS39FRzbaUfeXaibVNW\n" +
            "Wa3WAhBnWH6sDXf778CIPiZxNsvtmAqxBR9D/qtuANRBSVSn8G7N5DyhdOxqI0a7\n" +
            "RhQVByUAihNWFaaVwqRBzMaT3fOmsEKbINkHa1ivzSnfnFxKlXWFou+dxEa6Us8Q\n" +
            "BF4uzhhvVh/z9elGoXKDzoUBJVp+F769OOQwMJ2F0GwdZXzDxuyWBuvlSs59lAPE\n" +
            "KLDg4B3YmXtDCNsAIhmkvAVdSwR9euhLoJt9cf5Ygs5Jzhpo5UUhXQpli5+kGc9M\n" +
            "vmhQmtcNAoGBAP7UB6mxy4oMrq8m3XSGUMbWp8g4PgCVwMAZHcLdxBa+63eySJf1\n" +
            "k7Xje92FLCvKnvZkkJUuwAO56TiED79LH8WB8twaBifLGG81F/gz2au5Uk3ACcKG\n" +
            "x8udB8o5hGfLhRbjWLvB8M4Sz5/sFt8Pj+Hp93Xe6qT8OQUE/Ucx3RGTAoGBAMfz\n" +
            "IWJTrnERpDhLnbeFATOGj0mHHD/6tOI/RlelnGG/fsTrILiWlcl5zV3shfzQSbJk\n" +
            "2exX7XH/i0oNS8YFJFJJulGz/N6PKW4mHOvxRLzSA1mP0scX3kpNaScy7tn1jUcV\n" +
            "fDChe2KdSbDcdJxslR0+7iEd+vuaN0Glm6MuNtSvAoGBANlYTOTegUTQhllbo7yU\n" +
            "JudBBD/QQInNMGZnxauD7JCd7EXeKO57Ba44RVhuEnuTSTvMEEk9Y4aKFbIfaTL2\n" +
            "RzX1HQe/popgPgnz1erbhodh/CHWbjydKmGvIIrJvfWtb2lTJUaXJuUMxEuZWPSN\n" +
            "2GGOZYMCS03G1ndChygnDm/DAoGBAJoRC046cJWLJGCEU1iPUaekLVBYnTnnMe2L\n" +
            "F+Z9I4xmA4Y0LphM38nI4qdWkr+EtuSQtSJZdgp6/5blu9cvKxeE294Ms/HBmgjX\n" +
            "sT1UQMTFhfNC0QS7rXrPPxEHO+gSPvPg6DqIkwwfaiKvG+NT/2nzjYVTFos7/wvE\n" +
            "UbJg77rdAoGBAKMQF7Nd5sampKKxhBElgUYN+Jy1pnLrmRK65Oc5sQHFywfTsuqB\n" +
            "xCIaHo3ba+E+6CwdKWcSX8Mjz8+YJO6IiIxuUDdtMNxvwM0+A/eoPoS+JEhygNl1\n" +
            "2OuY+ITMFGhRJzQS3wvkNvkEubVsS96vKjWrE46hula0N7h0r+zWPs9W\n" +
            "-----END RSA PRIVATE KEY-----\n";

        PrivateKey loadedKey = authService.loadPrivateKey(pkcs1Pem);
        assertNotNull(loadedKey);
        assertEquals("RSA", loadedKey.getAlgorithm());
    }

    @Test
    void shouldThrowOnEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> authService.loadPrivateKey(null));
        assertThrows(IllegalArgumentException.class, () -> authService.loadPrivateKey("   "));
    }
}
