package prflow.spring_backend.engines.assignment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import prflow.spring_backend.engines.expertise.event.ExpertiseCalculatedEvent;
import prflow.spring_backend.enums.DeveloperSeniority;

class AssignmentServiceTest {

    private JdbcTemplate jdbcTemplate;
    private AssignmentScoringService scoringService;
    private AssignmentConfig config;
    private ApplicationEventPublisher eventPublisher;
    private AssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        scoringService = mock(AssignmentScoringService.class);
        config = new AssignmentConfig();
        eventPublisher = mock(ApplicationEventPublisher.class);
        assignmentService = new AssignmentService(jdbcTemplate, scoringService, config, eventPublisher);

        // Default mock behaviors
        when(scoringService.calculateScore(anyDouble(), anyInt())).thenAnswer(invocation -> {
            double exp = invocation.getArgument(0);
            int count = invocation.getArgument(1);
            return exp / (1.0 + count * 2.0);
        });

        when(scoringService.buildReason(anyDouble(), anyInt(), anyDouble()))
            .thenReturn("Mock Reason");
    }

    @Test
    void shouldExcludePrAuthorAndAssignBestCandidates() {
        Long pullRequestId = 100L;
        Long repositoryId = 1L;
        Long authorId = 99L; // author

        // Mock pull request metadata
        when(jdbcTemplate.query(
            eq("SELECT author_id, repository_id, complexity_score FROM pull_requests WHERE id = ?"),
            any(RowMapper.class),
            eq(pullRequestId)
        )).thenReturn(Collections.singletonList(new Object() {
            // matches record structure implicitly in mapped List
        }));
        
        // Let's refine the RowMapper returns to supply exact list maps:
        // Query 1: PR Meta
        // We can stub the query that maps to the internal record PullRequestState.
        // PullRequestState has: authorId, repositoryId, complexityScore
        // Let's simulate list returning this mock structure:
        // We'll mock the mapper return:
        List<Object> prMeta = new ArrayList<>();
        prMeta.add(new Object()); // list size 1
        // To bypass RowMapper stubbing directly, we can override query method or mock rowmapper mapping:
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("author_id")).thenReturn(authorId);
            when(rs.getLong("repository_id")).thenReturn(repositoryId);
            when(rs.getObject("complexity_score")).thenReturn(5.0);
            when(rs.getDouble("complexity_score")).thenReturn(5.0);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM pull_requests"),
            any(RowMapper.class),
            eq(pullRequestId)
        );

        // Query 2: Contributors list (d.id, d.username, d.seniority, rd.contribution_count)
        // Dev 99: Author (should be excluded)
        // Dev 10: MID, 5 contributions
        // Dev 20: SENIOR, 10 contributions
        // Dev 30: JUNIOR, 1 contribution
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs1 = mock(java.sql.ResultSet.class);
            when(rs1.getLong("id")).thenReturn(99L);
            when(rs1.getString("username")).thenReturn("author_dev");
            when(rs1.getString("seniority")).thenReturn("SENIOR");
            when(rs1.getInt("contribution_count")).thenReturn(50);

            java.sql.ResultSet rs2 = mock(java.sql.ResultSet.class);
            when(rs2.getLong("id")).thenReturn(10L);
            when(rs2.getString("username")).thenReturn("mid_dev");
            when(rs2.getString("seniority")).thenReturn("MID");
            when(rs2.getInt("contribution_count")).thenReturn(5);

            java.sql.ResultSet rs3 = mock(java.sql.ResultSet.class);
            when(rs3.getLong("id")).thenReturn(20L);
            when(rs3.getString("username")).thenReturn("senior_dev");
            when(rs3.getString("seniority")).thenReturn("SENIOR");
            when(rs3.getInt("contribution_count")).thenReturn(10);

            java.sql.ResultSet rs4 = mock(java.sql.ResultSet.class);
            when(rs4.getLong("id")).thenReturn(30L);
            when(rs4.getString("username")).thenReturn("junior_dev");
            when(rs4.getString("seniority")).thenReturn("JUNIOR");
            when(rs4.getInt("contribution_count")).thenReturn(1);

            List<Object> list = new ArrayList<>();
            list.add(mapper.mapRow(rs1, 0));
            list.add(mapper.mapRow(rs2, 1));
            list.add(mapper.mapRow(rs3, 2));
            list.add(mapper.mapRow(rs4, 3));
            return list;
        }).when(jdbcTemplate).query(
            contains("FROM repository_developers"),
            any(RowMapper.class),
            eq(repositoryId)
        );

        // Query 3: countActiveReviews counts
        when(jdbcTemplate.queryForObject(
            contains("FROM reviewer_assignments ra"),
            eq(Integer.class),
            anyLong()
        )).thenReturn(0);

        // Build expertise event
        // 10: score 5.0, 20: score 8.0, 30: score 2.0
        ExpertiseCalculatedEvent event = new ExpertiseCalculatedEvent(
            pullRequestId,
            repositoryId,
            Arrays.asList(10L, 20L, 30L),
            Arrays.asList(5.0, 8.0, 2.0)
        );

        // Turn off junior growth for standard flow
        config.setJuniorGrowthRatio(0.0);

        // Run assignment
        assignmentService.assignReviewers(event);

        // Verify Author (99L) was NOT assigned, and top-scored (20L and 10L) were chosen
        verify(jdbcTemplate, times(2)).update(
            contains("INSERT INTO reviewer_assignments"),
            eq(pullRequestId),
            anyLong(),
            anyDouble(),
            anyString(),
            anyString()
        );

        // Capture persisted assignments
        ArgumentCaptor<Long> devIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(jdbcTemplate, times(2)).update(
            anyString(),
            eq(pullRequestId),
            devIdCaptor.capture(),
            anyDouble(),
            anyString(),
            anyString()
        );

        List<Long> capturedIds = devIdCaptor.getAllValues();
        assertTrue(capturedIds.contains(20L), "Senior dev with highest expertise score should be assigned");
        assertTrue(capturedIds.contains(10L), "Mid dev with second highest expertise score should be assigned");
        assertFalse(capturedIds.contains(99L), "Author should be excluded from assignments");
        assertFalse(capturedIds.contains(30L), "Junior dev should not be chosen due to limit");

        // Verify Event Egress
        ArgumentCaptor<ReviewersAssignedEvent> eventCaptor = ArgumentCaptor.forClass(ReviewersAssignedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
        
        ReviewersAssignedEvent emitted = eventCaptor.getValue();
        assertEquals(pullRequestId, emitted.pullRequestId());
        assertEquals(repositoryId, emitted.repositoryId());
        assertTrue(emitted.assignedReviewerIds().contains(20L));
        assertTrue(emitted.assignedReviewerIds().contains(10L));
    }

    @Test
    void complexityGateShouldExcludeNonSeniorsWhenComplexityIsHigh() {
        Long pullRequestId = 100L;
        Long repositoryId = 1L;
        Long authorId = 99L;

        // Mock PR with HIGH complexity (8.5 > threshold 7.0)
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("author_id")).thenReturn(authorId);
            when(rs.getLong("repository_id")).thenReturn(repositoryId);
            when(rs.getObject("complexity_score")).thenReturn(8.5);
            when(rs.getDouble("complexity_score")).thenReturn(8.5);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM pull_requests"),
            any(RowMapper.class),
            eq(pullRequestId)
        );

        // Contributors: 99: author, 10: MID, 20: SENIOR, 25: SENIOR (another senior)
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs1 = mock(java.sql.ResultSet.class);
            when(rs1.getLong("id")).thenReturn(99L);
            when(rs1.getString("username")).thenReturn("author_dev");
            when(rs1.getString("seniority")).thenReturn("SENIOR");
            when(rs1.getInt("contribution_count")).thenReturn(50);

            java.sql.ResultSet rs2 = mock(java.sql.ResultSet.class);
            when(rs2.getLong("id")).thenReturn(10L);
            when(rs2.getString("username")).thenReturn("mid_dev");
            when(rs2.getString("seniority")).thenReturn("MID");
            when(rs2.getInt("contribution_count")).thenReturn(5);

            java.sql.ResultSet rs3 = mock(java.sql.ResultSet.class);
            when(rs3.getLong("id")).thenReturn(20L);
            when(rs3.getString("username")).thenReturn("senior_1");
            when(rs3.getString("seniority")).thenReturn("SENIOR");
            when(rs3.getInt("contribution_count")).thenReturn(10);

            java.sql.ResultSet rs4 = mock(java.sql.ResultSet.class);
            when(rs4.getLong("id")).thenReturn(25L);
            when(rs4.getString("username")).thenReturn("senior_2");
            when(rs4.getString("seniority")).thenReturn("SENIOR");
            when(rs4.getInt("contribution_count")).thenReturn(15);

            List<Object> list = new ArrayList<>();
            list.add(mapper.mapRow(rs1, 0));
            list.add(mapper.mapRow(rs2, 1));
            list.add(mapper.mapRow(rs3, 2));
            list.add(mapper.mapRow(rs4, 3));
            return list;
        }).when(jdbcTemplate).query(
            contains("FROM repository_developers"),
            any(RowMapper.class),
            eq(repositoryId)
        );

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong())).thenReturn(0);

        // Expertise scores: MID(10L) has 10.0, SENIORS have less
        ExpertiseCalculatedEvent event = new ExpertiseCalculatedEvent(
            pullRequestId,
            repositoryId,
            Arrays.asList(10L, 20L, 25L),
            Arrays.asList(10.0, 3.0, 4.0)
        );

        config.setJuniorGrowthRatio(0.0);

        assignmentService.assignReviewers(event);

        // Validate MID dev (10L) is EXCLUDED due to complexity gate, even with highest expertise score
        ArgumentCaptor<Long> devIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(jdbcTemplate, times(2)).update(
            anyString(),
            eq(pullRequestId),
            devIdCaptor.capture(),
            anyDouble(),
            anyString(),
            anyString()
        );

        List<Long> assignedIds = devIdCaptor.getAllValues();
        assertTrue(assignedIds.contains(20L));
        assertTrue(assignedIds.contains(25L));
        assertFalse(assignedIds.contains(10L), "MID dev must be excluded because PR complexity > 7.0");
    }

    @Test
    void fallbackRoutingShouldPreventAssignmentDeadEnds() {
        Long pullRequestId = 100L;
        Long repositoryId = 1L;
        Long authorId = 99L;

        // Mock PR metadata
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs = mock(java.sql.ResultSet.class);
            when(rs.getLong("author_id")).thenReturn(authorId);
            when(rs.getLong("repository_id")).thenReturn(repositoryId);
            when(rs.getObject("complexity_score")).thenReturn(2.0);
            when(rs.getDouble("complexity_score")).thenReturn(2.0);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(
            contains("FROM pull_requests"),
            any(RowMapper.class),
            eq(pullRequestId)
        );

        // Repository contributors: Author (99L) and fallback (44L, MID, 25 contributions)
        // There are no standard candidates with non-zero expertise score.
        doAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            java.sql.ResultSet rs1 = mock(java.sql.ResultSet.class);
            when(rs1.getLong("id")).thenReturn(99L);
            when(rs1.getString("username")).thenReturn("author_dev");
            when(rs1.getString("seniority")).thenReturn("SENIOR");
            when(rs1.getInt("contribution_count")).thenReturn(50);

            java.sql.ResultSet rs2 = mock(java.sql.ResultSet.class);
            when(rs2.getLong("id")).thenReturn(44L);
            when(rs2.getString("username")).thenReturn("fallback_contributor");
            when(rs2.getString("seniority")).thenReturn("MID");
            when(rs2.getInt("contribution_count")).thenReturn(25);

            List<Object> list = new ArrayList<>();
            list.add(mapper.mapRow(rs1, 0));
            list.add(mapper.mapRow(rs2, 1));
            return list;
        }).when(jdbcTemplate).query(
            contains("FROM repository_developers"),
            any(RowMapper.class),
            eq(repositoryId)
        );

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), anyLong())).thenReturn(0);

        // Empty expertise scores
        ExpertiseCalculatedEvent event = new ExpertiseCalculatedEvent(
            pullRequestId,
            repositoryId,
            Collections.emptyList(),
            Collections.emptyList()
        );

        assignmentService.assignReviewers(event);

        // Verify fallback developer (44L) was assigned via fallback routing
        ArgumentCaptor<Long> devIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);

        verify(jdbcTemplate, times(1)).update(
            anyString(),
            eq(pullRequestId),
            devIdCaptor.capture(),
            anyDouble(),
            reasonCaptor.capture(),
            typeCaptor.capture()
        );

        assertEquals(44L, devIdCaptor.getValue());
        assertEquals("FALLBACK", typeCaptor.getValue());
        assertTrue(reasonCaptor.getValue().contains("Fallback Level 3"));
    }
}
