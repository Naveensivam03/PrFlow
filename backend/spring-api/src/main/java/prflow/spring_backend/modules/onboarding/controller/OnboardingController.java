package prflow.spring_backend.modules.onboarding.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prflow.spring_backend.dto.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // Allows clean API connections from local React Dev servers
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final JdbcTemplate jdbcTemplate;

    public OnboardingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lists all repositories currently ingested via GitHub App webhook installations.
     */
    @GetMapping("/repositories")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRepositories() {
        // Query the most recently connected organization in the postgres schema
        String orgQuery = "SELECT name, github_installation_id FROM organizations LIMIT 1";
        List<Map<String, Object>> orgs = jdbcTemplate.queryForList(orgQuery);
        
        String orgName = "Naveensivam03";
        Long installationId = 12345678L;
        if (!orgs.isEmpty()) {
            orgName = (String) orgs.get(0).get("name");
            Object instId = orgs.get(0).get("github_installation_id");
            if (instId instanceof Number) {
                installationId = ((Number) instId).longValue();
            }
        }

        // Query all repositories in the database
        String repoQuery = "SELECT id, name, is_active FROM repositories ORDER BY name ASC";
        List<Map<String, Object>> repos = jdbcTemplate.queryForList(repoQuery);

        return ResponseEntity.ok(ApiResponse.success("Repositories loaded", Map.of(
            "organization", orgName,
            "installationId", installationId,
            "repositories", repos
        )));
    }

    /**
     * Activates the selected repositories by updating their status in the postgres database.
     */
    @PostMapping("/repositories/initialize")
    public ResponseEntity<ApiResponse<Void>> initializeRepositories(@RequestBody Map<String, List<Long>> request) {
        List<Long> repositoryIds = request.get("repositoryIds");
        if (repositoryIds != null && !repositoryIds.isEmpty()) {
            for (Long id : repositoryIds) {
                jdbcTemplate.update("UPDATE repositories SET is_active = TRUE WHERE id = ?", id);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("Repositories initialized successfully", null));
    }

    /**
     * Streams real-time sync metrics directly from actual database table states.
     */
    @GetMapping("/sync/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSyncStatus() {
        Long contributors = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM developers", Long.class);
        Long pullRequests = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pull_requests", Long.class);
        Long expertise = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM developer_file_expertise", Long.class);
        Long reviews = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pull_request_reviews", Long.class);

        return ResponseEntity.ok(ApiResponse.success("Sync status fetched", Map.of(
            "contributors", contributors != null ? contributors : 0L,
            "pullRequests", pullRequests != null ? pullRequests : 0L,
            "expertise", expertise != null ? expertise : 0L,
            "reviews", reviews != null ? reviews : 0L
        )));
    }
}
