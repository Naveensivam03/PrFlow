package prflow.spring_backend.engines.escalation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import prflow.spring_backend.engines.assignment.service.AssignmentService;
import prflow.spring_backend.engines.expertise.event.ExpertiseCalculatedEvent;

class EscalationServiceTest {

    private JdbcTemplate jdbcTemplate;
    private AssignmentService assignmentService;
    private EmailNotificationService emailNotificationService;
    private ApplicationEventPublisher eventPublisher;
    private EscalationService escalationService;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        assignmentService = mock(AssignmentService.class);
        emailNotificationService = mock(EmailNotificationService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        escalationService = new EscalationService(jdbcTemplate, assignmentService, emailNotificationService, eventPublisher);
    }

    @Test
    void shouldTriggerLevel1ReminderAt24Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = now.minusHours(25); // 25 hours ago

        // 1. Mock the query loading active assignments
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("pull_request_id")).thenReturn(100L);
            when(rs.getLong("developer_id")).thenReturn(10L);
            when(rs.getTimestamp("assignment_created_at")).thenReturn(java.sql.Timestamp.valueOf(createdTime));
            when(rs.getString("assignment_status")).thenReturn("ASSIGNED");
            when(rs.getInt("escalation_level")).thenReturn(0);
            when(rs.getString("username")).thenReturn("stalled_dev");
            when(rs.getString("pr_title")).thenReturn("Update Core Security");
            when(rs.getLong("github_pr_number")).thenReturn(42L);
            when(rs.getObject("complexity_score")).thenReturn(3.5);
            when(rs.getDouble("complexity_score")).thenReturn(3.5);
            when(rs.getString("repo_name")).thenReturn("auth-service");
            when(rs.getLong("repository_id")).thenReturn(1L);

            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM reviewer_assignments ra"),
            any(RowMapper.class)
        );

        // Mock database update statement to return 1 (success)
        when(jdbcTemplate.update(
            contains("UPDATE reviewer_assignments"),
            any(LocalDateTime.class),
            eq(100L),
            eq(10L)
        )).thenReturn(1);

        // 2. Run scan
        escalationService.checkEscalations(now);

        // 3. Verify status updated to REMINDER_SENT
        verify(jdbcTemplate, times(1)).update(
            contains("UPDATE reviewer_assignments SET   assignment_status = 'REMINDER_SENT'"),
            eq(now),
            eq(100L),
            eq(10L)
        );

        // 4. Verify email notification dispatched
        verify(emailNotificationService, times(1)).sendReminderEmail(
            eq("stalled_dev@company.com"),
            eq("stalled_dev"),
            eq("Update Core Security"),
            eq("auth-service"),
            eq(3.5),
            contains("auth-service/pull/42"),
            eq(createdTime),
            eq(25L)
        );

        // 5. Verify domain event emitted
        ArgumentCaptor<ReviewReminderSentEvent> eventCaptor = ArgumentCaptor.forClass(ReviewReminderSentEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        
        ReviewReminderSentEvent emitted = eventCaptor.getValue();
        assertEquals(100L, emitted.pullRequestId());
        assertEquals(10L, emitted.developerId());
        assertEquals("stalled_dev", emitted.reviewerUsername());
        assertEquals(now, emitted.sentAt());
    }

    @Test
    void shouldTriggerLevel2StaleAt36Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = now.minusHours(37); // 37 hours ago

        // 1. Mock the query loading active assignments
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("pull_request_id")).thenReturn(100L);
            when(rs.getLong("developer_id")).thenReturn(10L);
            when(rs.getTimestamp("assignment_created_at")).thenReturn(java.sql.Timestamp.valueOf(createdTime));
            when(rs.getString("assignment_status")).thenReturn("REMINDER_SENT");
            when(rs.getInt("escalation_level")).thenReturn(1);
            when(rs.getString("username")).thenReturn("stalled_dev");
            when(rs.getString("pr_title")).thenReturn("Update Core Security");
            when(rs.getLong("github_pr_number")).thenReturn(42L);
            when(rs.getObject("complexity_score")).thenReturn(3.5);
            when(rs.getDouble("complexity_score")).thenReturn(3.5);
            when(rs.getString("repo_name")).thenReturn("auth-service");
            when(rs.getLong("repository_id")).thenReturn(1L);

            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM reviewer_assignments ra"),
            any(RowMapper.class)
        );

        // Mock database update
        when(jdbcTemplate.update(
            contains("UPDATE reviewer_assignments"),
            any(LocalDateTime.class),
            eq(100L),
            eq(10L)
        )).thenReturn(1);

        // 2. Run scan
        escalationService.checkEscalations(now);

        // 3. Verify status updated to STALE
        verify(jdbcTemplate, times(1)).update(
            contains("UPDATE reviewer_assignments SET   assignment_status = 'STALE'"),
            eq(now),
            eq(100L),
            eq(10L)
        );

        // 4. Verify stale email sent
        verify(emailNotificationService, times(1)).sendStaleEmail(
            eq("stalled_dev-manager@company.com"),
            eq("stalled_dev"),
            eq("Update Core Security"),
            eq("auth-service"),
            eq(37L)
        );

        // 5. Verify domain event emitted
        ArgumentCaptor<PullRequestStaleEvent> eventCaptor = ArgumentCaptor.forClass(PullRequestStaleEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        
        PullRequestStaleEvent emitted = eventCaptor.getValue();
        assertEquals(100L, emitted.pullRequestId());
        assertEquals(10L, emitted.developerId());
        assertEquals("stalled_dev", emitted.reviewerUsername());
        assertEquals(now, emitted.escalatedAt());
    }

    @Test
    void shouldTriggerLevel3ReassignmentAt48Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = now.minusHours(50); // 50 hours ago

        // 1. Mock the query loading active assignments
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("pull_request_id")).thenReturn(100L);
            when(rs.getLong("developer_id")).thenReturn(10L);
            when(rs.getTimestamp("assignment_created_at")).thenReturn(java.sql.Timestamp.valueOf(createdTime));
            when(rs.getString("assignment_status")).thenReturn("STALE");
            when(rs.getInt("escalation_level")).thenReturn(2);
            when(rs.getString("username")).thenReturn("stalled_dev");
            when(rs.getString("pr_title")).thenReturn("Update Core Security");
            when(rs.getLong("github_pr_number")).thenReturn(42L);
            when(rs.getObject("complexity_score")).thenReturn(3.5);
            when(rs.getDouble("complexity_score")).thenReturn(3.5);
            when(rs.getString("repo_name")).thenReturn("auth-service");
            when(rs.getLong("repository_id")).thenReturn(1L);

            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM reviewer_assignments ra"),
            any(RowMapper.class)
        );

        // 2. Mock query for other active reviewer IDs (returns empty)
        when(jdbcTemplate.query(
            contains("SELECT developer_id FROM reviewer_assignments"),
            any(RowMapper.class),
            eq(100L),
            eq(10L)
        )).thenReturn(Collections.emptyList());

        // Mock state transition update to REASSIGNED
        when(jdbcTemplate.update(
            contains("UPDATE reviewer_assignments SET   assignment_status = 'REASSIGNED'"),
            any(LocalDateTime.class),
            eq(100L),
            eq(10L)
        )).thenReturn(1);

        // 3. Mock query for changed files
        when(jdbcTemplate.query(
            contains("SELECT file_path FROM pull_request_files"),
            any(RowMapper.class),
            eq(100L)
        )).thenReturn(Arrays.asList("src/main/java/Security.java"));

        // 4. Mock expertise aggregation query
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("developer_id")).thenReturn(20L); // candidate replacement
            when(rs.getDouble("cumulative_score")).thenReturn(15.0);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM developer_file_expertise"),
            any(RowMapper.class),
            any(Object[].class)
        );

        // 5. Mock current active reviewers after Assignment Engine re-run (returns new assignee)
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("developer_id")).thenReturn(20L);
            when(rs.getString("username")).thenReturn("replacement_dev");
            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("SELECT ra.developer_id, d.username FROM reviewer_assignments ra"),
            any(RowMapper.class),
            eq(100L)
        );

        // 6. Run scan
        escalationService.checkEscalations(now);

        // 7. Verify assignment status updated to REASSIGNED
        verify(jdbcTemplate, times(1)).update(
            contains("UPDATE reviewer_assignments SET   assignment_status = 'REASSIGNED'"),
            eq(now),
            eq(100L),
            eq(10L)
        );

        // 8. Verify AssignmentService invoked with rebuild ExpertiseCalculatedEvent excluding stalled reviewer (10L)
        ArgumentCaptor<ExpertiseCalculatedEvent> eventCaptor = ArgumentCaptor.forClass(ExpertiseCalculatedEvent.class);
        ArgumentCaptor<List<Long>> exclusionsCaptor = ArgumentCaptor.forClass(List.class);
        verify(assignmentService, times(1)).assignReviewers(eventCaptor.capture(), exclusionsCaptor.capture());

        ExpertiseCalculatedEvent rebuildEvent = eventCaptor.getValue();
        assertEquals(100L, rebuildEvent.pullRequestId());
        assertEquals(1L, rebuildEvent.repositoryId());
        assertEquals(Collections.singletonList(20L), rebuildEvent.expertiseCandidates());
        assertEquals(Collections.singletonList(15.0), rebuildEvent.expertiseScores());
        
        List<Long> exclusions = exclusionsCaptor.getValue();
        assertEquals(Collections.singletonList(10L), exclusions);

        // 9. Verify reassignment email dispatched
        verify(emailNotificationService, times(1)).sendReassignmentEmail(
            eq("stalled_dev@company.com"),
            eq("stalled_dev"),
            eq("replacement_dev@company.com"),
            eq("replacement_dev"),
            eq("Update Core Security"),
            eq("auth-service")
        );

        // 10. Verify reassigned domain event emitted
        ArgumentCaptor<ReviewerReassignedEvent> reassignedEventCaptor = ArgumentCaptor.forClass(ReviewerReassignedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(reassignedEventCaptor.capture());

        ReviewerReassignedEvent reassigned = reassignedEventCaptor.getValue();
        assertEquals(100L, reassigned.pullRequestId());
        assertEquals(1L, reassigned.repositoryId());
        assertEquals(10L, reassigned.previousReviewerId());
        assertEquals(20L, reassigned.newReviewerId());
        assertEquals(now, reassigned.reassignedAt());
    }

    @Test
    void shouldNotReTriggerIfAlreadyProcessedAtLevel() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = now.minusHours(25); // 25 hours ago, but already Level 1 processed

        // 1. Mock active assignment with escalation_level = 1, status = REMINDER_SENT
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("pull_request_id")).thenReturn(100L);
            when(rs.getLong("developer_id")).thenReturn(10L);
            when(rs.getTimestamp("assignment_created_at")).thenReturn(java.sql.Timestamp.valueOf(createdTime));
            when(rs.getString("assignment_status")).thenReturn("REMINDER_SENT");
            when(rs.getInt("escalation_level")).thenReturn(1);
            when(rs.getString("username")).thenReturn("stalled_dev");
            when(rs.getString("pr_title")).thenReturn("Update Core Security");
            when(rs.getLong("github_pr_number")).thenReturn(42L);
            when(rs.getObject("complexity_score")).thenReturn(3.5);
            when(rs.getDouble("complexity_score")).thenReturn(3.5);
            when(rs.getString("repo_name")).thenReturn("auth-service");
            when(rs.getLong("repository_id")).thenReturn(1L);

            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM reviewer_assignments ra"),
            any(RowMapper.class)
        );

        // 2. Run scan
        escalationService.checkEscalations(now);

        // 3. Verify no database updates, emails, or events are processed
        verify(jdbcTemplate, never()).update(anyString(), any(LocalDateTime.class), anyLong(), anyLong());
        verify(emailNotificationService, never()).sendReminderEmail(anyString(), anyString(), anyString(), anyString(), anyDouble(), anyString(), any(LocalDateTime.class), anyLong());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
