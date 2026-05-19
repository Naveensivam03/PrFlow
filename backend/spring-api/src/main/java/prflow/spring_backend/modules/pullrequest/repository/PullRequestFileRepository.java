package prflow.spring_backend.modules.pullrequest.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import prflow.spring_backend.modules.pullrequest.domain.PullRequestFile;

public interface PullRequestFileRepository extends JpaRepository<PullRequestFile, Long> {

    List<PullRequestFile> findByPullRequestId(Long pullRequestId);

    List<PullRequestFile> findByScopeIdentifier(String scopeIdentifier);

    boolean existsByPullRequestIdAndFilePath(Long pullRequestId, String filePath);
}
