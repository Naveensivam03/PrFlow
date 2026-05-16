package prflow.spring_backend.modules.organization.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prflow.spring_backend.modules.organization.domain.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByGithubInstallationId(Long githubInstallationId);

    Optional<Organization> findByGithubOrgId(Long githubOrgId);
}
