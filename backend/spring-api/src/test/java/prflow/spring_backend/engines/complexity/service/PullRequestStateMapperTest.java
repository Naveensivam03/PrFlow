package prflow.spring_backend.engines.complexity.service;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for the private PullRequestStateMapper nested inside ComplexityService.
 * Uses reflection to instantiate the private mapper and to inspect the private record.
 */
public class PullRequestStateMapperTest {

    @Test
    void mapsColumnsToRecord() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("complexity_score")).thenReturn(Double.valueOf(12.5));
        when(rs.getDouble("complexity_score")).thenReturn(12.5);
        when(rs.getString("complexity_level")).thenReturn("MEDIUM");
        LocalDateTime dt = LocalDateTime.of(2026, 5, 30, 12, 0);
        when(rs.getTimestamp("complexity_calculated_at")).thenReturn(Timestamp.valueOf(dt));

        // Instantiate the private nested mapper class via reflection
        Class<?> mapperClass = Class.forName("prflow.spring_backend.engines.complexity.service.ComplexityService$PullRequestStateMapper");
        Object mapperInstance = mapperClass.getDeclaredConstructor().newInstance();
        @SuppressWarnings("unchecked")
        RowMapper<Object> mapper = (RowMapper<Object>) mapperInstance;

        Object prState = mapper.mapRow(rs, 0);
        assertNotNull(prState, "mapper should return a non-null record instance");

        // PullRequestState is a private nested record; use reflection to access its accessors
        Class<?> prStateClass = prState.getClass();
        java.lang.reflect.Method mScore = prStateClass.getDeclaredMethod("complexityScore");
        java.lang.reflect.Method mLevel = prStateClass.getDeclaredMethod("complexityLevel");
        java.lang.reflect.Method mCalcAt = prStateClass.getDeclaredMethod("complexityCalculatedAt");
        mScore.setAccessible(true);
        mLevel.setAccessible(true);
        mCalcAt.setAccessible(true);

        Double score = (Double) mScore.invoke(prState);
        String level = (String) mLevel.invoke(prState);
        LocalDateTime calcAt = (LocalDateTime) mCalcAt.invoke(prState);

        assertEquals(12.5, score);
        assertEquals("MEDIUM", level);
        assertEquals(dt, calcAt);
    }
}
