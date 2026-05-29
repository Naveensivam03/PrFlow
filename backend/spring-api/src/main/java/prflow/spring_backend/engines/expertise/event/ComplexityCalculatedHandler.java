package prflow.spring_backend.engines.expertise.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import prflow.spring_backend.engines.complexity.event.ComplexityCalculatedEvent;

/**
 * Orchestration listener that chains Expertise accumulation directly behind Complexity calculation.
 */
@Component
public class ComplexityCalculatedHandler {

    private static final Logger logger = LoggerFactory.getLogger(ComplexityCalculatedHandler.class);
    private final ExpertiseService expertiseService;

    public ComplexityCalculatedHandler(ExpertiseService expertiseService) {
        this.expertiseService = expertiseService;
    }

    @EventListener
    public void onComplexityCalculated(ComplexityCalculatedEvent event) {
        logger.info("Expertise engine ingress triggered via COMPLEXITY_CALCULATED: pullRequestId={} repositoryId={}",
            event.pullRequestId(), event.repositoryId());
        
        System.out.println("[DEBUG][ExpertiseEngine] event.received pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId());
        
        expertiseService.handle(event);
    }
}
