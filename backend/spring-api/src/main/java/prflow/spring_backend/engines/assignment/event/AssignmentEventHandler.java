package prflow.spring_backend.engines.assignment.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import prflow.spring_backend.engines.expertise.event.ExpertiseCalculatedEvent;

/**
 * Orchestrator listener that chains Downstream Reviewer Assignment directly behind Expertise engine calculations.
 */
@Component
public class AssignmentEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentEventHandler.class);
    private final AssignmentService assignmentService;

    public AssignmentEventHandler(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @EventListener
    public void onExpertiseCalculated(ExpertiseCalculatedEvent event) {
        System.out.println("[DEBUG][AssignmentEngine] event.received pullRequestId=" + event.pullRequestId());
        logger.info("Assignment engine ingress triggered via EXPERTISE_CALCULATED: pullRequestId={} repositoryId={}",
            event.pullRequestId(), event.repositoryId());

        assignmentService.assignReviewers(event);
    }
}
