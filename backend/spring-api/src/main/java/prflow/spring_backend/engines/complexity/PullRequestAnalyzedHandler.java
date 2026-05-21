package prflow.spring_backend.engines.complexity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Engine ingress for analyzed pull-request events.
 *
 * <p>This handler is intentionally thin: validate event boundary, log receipt,
 * and delegate deterministic enrichment to {@link ComplexityService}.
 */
@Component
public class PullRequestAnalyzedHandler {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestAnalyzedHandler.class);

    private final ComplexityService complexityService;

    public PullRequestAnalyzedHandler(ComplexityService complexityService) {
        this.complexityService = complexityService;
    }

    @EventListener
    public void onPullRequestAnalyzed(PullRequestAnalyzedEvent event) {
        // TEMP DEBUG: remove after live PR verification.
        System.out.println("[DEBUG][ComplexityEngine] event.received pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId() + " deliveryId=" + event.deliveryId());
        logger.info("complexity.engine.event.received pullRequestId={} repositoryId={} deliveryId={}",
            event.pullRequestId(), event.repositoryId(), event.deliveryId());
        complexityService.handle(event);
    }

    /**
     * Internal orchestration event consumed by the complexity engine.
     */
    public record PullRequestAnalyzedEvent(Long pullRequestId, Long repositoryId, String deliveryId) {}
}
