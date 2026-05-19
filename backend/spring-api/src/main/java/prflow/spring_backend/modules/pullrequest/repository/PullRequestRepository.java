package prflow.spring_backend.modules.pullrequest.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prflow.spring_backend.enums.PullRequestStatus;
import prflow.spring_backend.modules.pullrequest.domain.PullRequest;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    Optional<PullRequest> findByRepositoryIdAndGithubPrNumber(Long repositoryId, Long githubPrNumber);

    boolean existsByRepositoryIdAndGithubPrNumber(Long repositoryId, Long githubPrNumber);

    List<PullRequest> findByRepositoryIdAndStatusOrderByOpenedAtAsc(Long repositoryId, PullRequestStatus status);

    List<PullRequest> findByAuthorIdOrderByOpenedAtDesc(Long authorId);
}
