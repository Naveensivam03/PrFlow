package prflow.spring_backend.engines.escalation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Provider-agnostic service responsible for dispatching SLA notifications.
 * Generates clear, production-ready console templates and structured logs.
 */
@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    public void sendReminderEmail(
        String recipientEmail,
        String reviewerUsername,
        String prTitle,
        String repoName,
        double complexityScore,
        String reviewLink,
        LocalDateTime assignedAt,
        long hoursWaiting
    ) {
        String emailBody = String.format(
            """
            =========================================
            EMAIL NOTIFICATION: REVIEW REMINDER (24H SLA)
            =========================================
            To: %s
            Subject: [PRFlow] Action Required: Review Reminder for %s
            
            Hi %s,
            
            This is an automated reminder that you have a pending pull request review assignment that has been waiting for %d hours.
            
            Details:
            - Pull Request: %s
            - Repository: %s
            - Complexity Level: %.2f
            - Assigned At: %s
            - Review Link: %s
            
            Please complete your review as soon as possible to avoid delaying the workflow.
            
            Thank you,
            PRFlow SLA Orchestrator
            =========================================
            """,
            recipientEmail,
            prTitle,
            reviewerUsername,
            hoursWaiting,
            prTitle,
            repoName,
            complexityScore,
            assignedAt,
            reviewLink
        );

        System.out.print(emailBody);
        logger.info("email.sent type=REMINDER recipient={} prTitle={} repoName={} hoursWaiting={}",
            recipientEmail, prTitle, repoName, hoursWaiting);
    }

    public void sendStaleEmail(
        String recipientEmail,
        String reviewerUsername,
        String prTitle,
        String repoName,
        long hoursWaiting
    ) {
        String emailBody = String.format(
            """
            =========================================
            EMAIL NOTIFICATION: PULL REQUEST STALE (36H SLA)
            =========================================
            To: %s (Manager/Devs Channel)
            Subject: [PRFlow ALERT] Review Bottleneck: Stale Pull Request in %s
            
            Attention,
            
            The pull request review assignment for %s by reviewer %s has crossed the 36-hour threshold and is now marked as STALE.
            
            Details:
            - Pull Request: %s
            - Repository: %s
            - Assigned Reviewer: %s
            - Duration Waiting: %d hours
            
            A reviewer reassignment will occur in 12 hours if this bottleneck is not resolved immediately.
            
            Thank you,
            PRFlow SLA Orchestrator
            =========================================
            """,
            recipientEmail,
            repoName,
            prTitle,
            reviewerUsername,
            prTitle,
            repoName,
            reviewerUsername,
            hoursWaiting
        );

        System.out.print(emailBody);
        logger.info("email.sent type=STALE recipient={} prTitle={} repoName={} hoursWaiting={}",
            recipientEmail, prTitle, repoName, hoursWaiting);
    }

    public void sendReassignmentEmail(
        String previousReviewerEmail,
        String previousReviewerName,
        String newReviewerEmail,
        String newReviewerName,
        String prTitle,
        String repoName
    ) {
        String emailBody = String.format(
            """
            =========================================
            EMAIL NOTIFICATION: REVIEW REASSIGNMENT (48H SLA)
            =========================================
            To: %s, %s
            Subject: [PRFlow] Pull Request Reassignment: %s in %s
            
            SLA Orchestration Update:
            
            Pull request "%s" has exceeded the 48-hour response limit and has been automatically reassigned.
            
            - Unassigned Reviewer: %s (%s)
            - Newly Assigned Reviewer: %s (%s)
            - Repository: %s
            
            Please adjust your workloads accordingly.
            
            Thank you,
            PRFlow SLA Orchestrator
            =========================================
            """,
            previousReviewerEmail,
            newReviewerEmail,
            prTitle,
            repoName,
            prTitle,
            previousReviewerName,
            previousReviewerEmail,
            newReviewerName,
            newReviewerEmail,
            repoName
        );

        System.out.print(emailBody);
        logger.info("email.sent type=REASSIGNMENT previousReviewer={} newReviewer={} prTitle={} repoName={}",
            previousReviewerEmail, newReviewerEmail, prTitle, repoName);
    }
}
