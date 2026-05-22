package prflow.spring_backend.modules.pullrequest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import prflow.spring_backend.config.GitHubAuthService;
import prflow.spring_backend.engines.expertise.ReviewExpertiseEnricher;
import prflow.spring_backend.modules.pullrequest.event.ReviewsSynchronizedEvent;

class ReviewSyncServiceTest {

    private JdbcTemplate jdbcTemplate;
    private GitHubAuthService authService;
    private GitHubReviewFetcher reviewFetcher;
    private ReviewExpertiseEnricher expertiseEnricher;
    private ApplicationEventPublisher eventPublisher;
    private ReviewSyncService reviewSyncService;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        authService = mock(GitHubAuthService.class);
        reviewFetcher = mock(GitHubReviewFetcher.class);
        expertiseEnricher = mock(ReviewExpertiseEnricher.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        reviewSyncService = new ReviewSyncService(
            jdbcTemplate, authService, reviewFetcher, expertiseEnricher, eventPublisher
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFetchReviewsAndUpsertReviewersAndPublishEvent() {
        Long pullRequestId = 10L;
        Long repositoryId = 1L;
        Long organizationId = 2L;

        // 1. Mock PullRequestSyncMeta query response
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("github_pr_number")).thenReturn(15L);
            when(rs.getLong("github_repo_id")).thenReturn(999L);
            when(rs.getString("repo_name")).thenReturn("prflow");
            when(rs.getLong("github_installation_id")).thenReturn(111L);
            when(rs.getString("org_name")).thenReturn("Naveensivam03");
            when(rs.getLong("repository_id")).thenReturn(repositoryId);
            when(rs.getLong("organization_id")).thenReturn(organizationId);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM pull_requests pr"),
            any(RowMapper.class),
            eq(pullRequestId)
        );

        // 2. Mock GitHub App Token retrieval
        when(authService.getInstallationToken(eq(111L))).thenReturn("mock-installation-token");

        // 3. Mock reviews fetching
        List<GitHubReviewFetcher.GitHubReviewDto> mockReviews = new ArrayList<>();
        mockReviews.add(new GitHubReviewFetcher.GitHubReviewDto(
            5001L,
            new GitHubReviewFetcher.GitHubUserDto(888L, "reviewer_dev_1", "https://avatar.url/1"),
            "APPROVED",
            "Looks great! Approved.",
            "2026-05-22T16:00:00Z"
        ));
        mockReviews.add(new GitHubReviewFetcher.GitHubReviewDto(
            5002L,
            new GitHubReviewFetcher.GitHubUserDto(889L, "reviewer_dev_2", "https://avatar.url/2"),
            "COMMENTED",
            "Just a minor suggestion.",
            "2026-05-22T16:15:00Z"
        ));

        when(reviewFetcher.fetchReviews(
            eq("Naveensivam03"),
            eq("prflow"),
            eq(15L),
            eq("mock-installation-token")
        )).thenReturn(mockReviews);

        // 4. Mock developer upsert returns (returning Developer PKs)
        when(jdbcTemplate.queryForObject(
            contains("INSERT INTO developers"),
            eq(Long.class),
            eq(organizationId),
            eq(888L),
            eq("reviewer_dev_1"),
            eq("reviewer_dev_1"),
            eq("https://avatar.url/1")
        )).thenReturn(20L); // Reviewer 1 PK

        when(jdbcTemplate.queryForObject(
            contains("INSERT INTO developers"),
            eq(Long.class),
            eq(organizationId),
            eq(889L),
            eq("reviewer_dev_2"),
            eq("reviewer_dev_2"),
            eq("https://avatar.url/2")
        )).thenReturn(21L); // Reviewer 2 PK

        // Mock replay detection query returning empty list (meaning first-time ingestion)
        when(jdbcTemplate.query(
            contains("SELECT review_state FROM pull_request_reviews"),
            any(RowMapper.class),
            anyLong()
        )).thenReturn(Collections.emptyList());

        // 5. Run review sync
        reviewSyncService.synchronizeReviews(pullRequestId);

        // 6. Verify developer upserts happened
        verify(jdbcTemplate, times(2)).queryForObject(
            contains("INSERT INTO developers"),
            eq(Long.class),
            anyLong(),
            anyLong(),
            anyString(),
            anyString(),
            anyString()
        );

        // 7. Verify review upserts happened
        verify(jdbcTemplate, times(2)).update(
            contains("INSERT INTO pull_request_reviews"),
            eq(pullRequestId),
            anyLong(),
            anyLong(),
            anyString(),
            any(),
            any()
        );

        // 8. Verify expertise enricher is called for each unique reviewer
        verify(expertiseEnricher, times(1)).enrichDeveloperExpertise(eq(20L), eq(repositoryId), eq(pullRequestId));
        verify(expertiseEnricher, times(1)).enrichDeveloperExpertise(eq(21L), eq(repositoryId), eq(pullRequestId));

        // 9. Verify downstream event emitted
        ArgumentCaptor<ReviewsSynchronizedEvent> captor = ArgumentCaptor.forClass(ReviewsSynchronizedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());

        ReviewsSynchronizedEvent emitted = captor.getValue();
        assertEquals(pullRequestId, emitted.pullRequestId());
        assertEquals(repositoryId, emitted.repositoryId());
        assertEquals(2, emitted.synchronizedReviewerIds().size());
        assertTrue(emitted.synchronizedReviewerIds().contains(20L));
        assertTrue(emitted.synchronizedReviewerIds().contains(21L));
    }
}
