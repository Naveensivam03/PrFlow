package prflow.spring_backend.modules.pullrequest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubReviewFetcher {

    private static final Logger logger = LoggerFactory.getLogger(GitHubReviewFetcher.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record GitHubReviewDto(
        Long id,
        GitHubUserDto user,
        String state,
        String body,
        String submitted_at
    ) {}

    public record GitHubUserDto(
        Long id,
        String login,
        String avatar_url
    ) {}

    /**
     * Fetches reviews for a given repository and pull request from the GitHub API.
     * Supports full pagination.
     */
    public List<GitHubReviewDto> fetchReviews(String owner, String repo, Long prNumber, String token) {
        logger.info("reviews.sync.fetch.started owner={} repo={} prNumber={}", owner, repo, prNumber);
        List<GitHubReviewDto> allReviews = new ArrayList<>();
        int page = 1;

        try {
            while (true) {
                String url = String.format("https://api.github.com/repos/%s/%s/pulls/%d/reviews?per_page=100&page=%d",
                    owner, repo, prNumber, page);

                logger.debug("Fetching page {} from GitHub URL={}", page, url);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "prflow-spring-api")
                    .GET()
                    .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    logger.error("GitHub API reviews request failed status={} url={} body={}", 
                        response.statusCode(), url, response.body());
                    throw new RuntimeException("Failed to fetch reviews: HTTP " + response.statusCode() + " - " + response.body());
                }

                List<GitHubReviewDto> pageReviews = objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<GitHubReviewDto>>() {}
                );

                if (pageReviews == null || pageReviews.isEmpty()) {
                    break;
                }

                allReviews.addAll(pageReviews);
                logger.info("reviews.fetched owner={} repo={} prNumber={} count={} page={}", 
                    owner, repo, prNumber, pageReviews.size(), page);

                if (pageReviews.size() < 100) {
                    break;
                }
                page++;
            }

            logger.info("reviews.sync.fetch.completed owner={} repo={} prNumber={} totalCount={}", 
                owner, repo, prNumber, allReviews.size());
            return allReviews;

        } catch (Exception e) {
            logger.error("Failed to fetch reviews from GitHub API for {}/{} #{}", owner, repo, prNumber, e);
            throw new RuntimeException("Error fetching reviews for pull request: " + prNumber, e);
        }
    }
}
