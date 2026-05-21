package prflow.spring_backend.modules.pullrequest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prflow.spring_backend.dto.ApiResponse;
import prflow.spring_backend.engines.complexity.PullRequestAnalyzedHandler.PullRequestAnalyzedEvent;

/**
 * External REST gateway for PRFlow lifecycle events.
 * Accepts POST requests from ingestion systems and propagates them to internal Spring engines.
 */
@RestController
@RequestMapping("/api/events")
public class PullRequestEventController {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestEventController.class);
    private final ApplicationEventPublisher eventPublisher;

    public PullRequestEventController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/pull-request-analyzed")
    public ResponseEntity<ApiResponse<Void>> handlePullRequestAnalyzed(@RequestBody EventRequest request) {
        logger.info("External ingress PULL_REQUEST_ANALYZED received: pullRequestId={} repositoryId={} deliveryId={}",
            request.pullRequestId(), request.repositoryId(), request.deliveryId());

        PullRequestAnalyzedEvent event = new PullRequestAnalyzedEvent(
            request.pullRequestId(),
            request.repositoryId(),
            request.deliveryId()
        );

        eventPublisher.publishEvent(event);

        return ResponseEntity.ok(ApiResponse.success("Event received and propagated", null));
    }

    /**
     * Ingress DTO decoupling external event structure from internal application events.
     */
    public record EventRequest(Long pullRequestId, Long repositoryId, String deliveryId) {}
}
