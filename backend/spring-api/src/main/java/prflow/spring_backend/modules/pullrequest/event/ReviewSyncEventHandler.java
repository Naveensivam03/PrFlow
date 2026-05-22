package prflow.spring_backend.modules.pullrequest.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import prflow.spring_backend.engines.complexity.PullRequestAnalyzedHandler.PullRequestAnalyzedEvent;
import prflow.spring_backend.modules.pullrequest.service.ReviewSyncService;

@Component
public class ReviewSyncEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReviewSyncEventHandler.class);

    private final ReviewSyncService reviewSyncService;

    public ReviewSyncEventHandler(ReviewSyncService reviewSyncService) {
        this.reviewSyncService = reviewSyncService;
    }

    @EventListener
    public void onReviewSubmitted(ReviewSubmittedEvent event) {
        System.out.println("[DEBUG][ReviewSyncEngine] event.received REVIEW_SUBMITTED pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId() + " deliveryId=" + event.deliveryId());
        logger.info("review.sync.event.received eventType=REVIEW_SUBMITTED pullRequestId={} repositoryId={} deliveryId={}",
            event.pullRequestId(), event.repositoryId(), event.deliveryId());
        
        try {
            reviewSyncService.synchronizeReviews(event.pullRequestId());
        } catch (Exception e) {
            logger.error("failures review sync trigger failed for REVIEW_SUBMITTED pullRequestId={}", event.pullRequestId(), e);
        }
    }

    @EventListener
    public void onPullRequestAnalyzed(PullRequestAnalyzedEvent event) {
        System.out.println("[DEBUG][ReviewSyncEngine] event.received PULL_REQUEST_ANALYZED pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId() + " deliveryId=" + event.deliveryId());
        logger.info("review.sync.event.received eventType=PULL_REQUEST_ANALYZED pullRequestId={} repositoryId={} deliveryId={}",
            event.pullRequestId(), event.repositoryId(), event.deliveryId());
        
        try {
            reviewSyncService.synchronizeReviews(event.pullRequestId());
        } catch (Exception e) {
            logger.error("failures review sync trigger failed for PULL_REQUEST_ANALYZED pullRequestId={}", event.pullRequestId(), e);
        }
    }

    @EventListener
    public void onPullRequestMerged(PullRequestMergedEvent event) {
        System.out.println("[DEBUG][ReviewSyncEngine] event.received PULL_REQUEST_MERGED pullRequestId=" + event.pullRequestId()
            + " repositoryId=" + event.repositoryId() + " deliveryId=" + event.deliveryId());
        logger.info("review.sync.event.received eventType=PULL_REQUEST_MERGED pullRequestId={} repositoryId={} deliveryId={}",
            event.pullRequestId(), event.repositoryId(), event.deliveryId());
        
        try {
            reviewSyncService.synchronizeReviews(event.pullRequestId());
        } catch (Exception e) {
            logger.error("failures review sync trigger failed for PULL_REQUEST_MERGED pullRequestId={}", event.pullRequestId(), e);
        }
    }
}
