package prflow.spring_backend.modules.pullrequest.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prflow.spring_backend.modules.pullrequest.domain.PullRequestReview;

public interface PullRequestReviewRepository extends JpaRepository<PullRequestReview, Long> {

    List<PullRequestReview> findByPullRequestId(Long pullRequestId);

    Optional<PullRequestReview> findByGithubReviewId(Long githubReviewId);

    boolean existsByGithubReviewId(Long githubReviewId);
}
