package prflow.spring_backend.modules.developer.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prflow.spring_backend.modules.developer.domain.Developer;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Optional<Developer> findByOrganizationIdAndGithubUserId(Long organizationId, Long githubUserId);

    List<Developer> findByOrganizationId(Long organizationId);
}
