package prflow.spring_backend.modules.repository.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prflow.spring_backend.modules.repository.domain.Repository;

public interface RepositoryJpaRepository extends JpaRepository<Repository, Long> {

    Optional<Repository> findByOrganizationIdAndGithubRepoId(Long organizationId, Long githubRepoId);

    List<Repository> findByOrganizationId(Long organizationId);
}
