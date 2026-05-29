.
├── backend
│   └── spring-api
│       ├── HELP.md
│       ├── mvnw
│       ├── mvnw.cmd
│       ├── pom.xml
│       ├── src
│       │   ├── main
│       │   │   ├── java
│       │   │   │   └── prflow
│       │   │   │       └── spring_backend
│       │   │   │           ├── common
│       │   │   │           ├── config
│       │   │   │           │   ├── GitHubAuthService.java
│       │   │   │           │   ├── JpaAuditingConfig.java
│       │   │   │           │   └── SecurityConfig.java
│       │   │   │           ├── dto
│       │   │   │           │   └── ApiResponse.java
│       │   │   │           ├── engines
│       │   │   │           │   ├── assignment
│       │   │   │           │   │   ├── AssignmentConfig.java
│       │   │   │           │   │   ├── AssignmentEventHandler.java
│       │   │   │           │   │   ├── AssignmentScoringService.java
│       │   │   │           │   │   ├── AssignmentService.java
│       │   │   │           │   │   ├── dto
│       │   │   │           │   │   │   └── ReviewerAssignmentDto.java
│       │   │   │           │   │   ├── model
│       │   │   │           │   │   │   └── ReviewerCandidate.java
│       │   │   │           │   │   └── ReviewersAssignedEvent.java
│       │   │   │           │   ├── complexity
│       │   │   │           │   │   ├── ComplexityBreakdown.java
│       │   │   │           │   │   ├── ComplexityCalculatedEvent.java
│       │   │   │           │   │   ├── ComplexityCalculator.java
│       │   │   │           │   │   ├── ComplexityConfig.java
│       │   │   │           │   │   ├── ComplexityLevel.java
│       │   │   │           │   │   ├── ComplexityService.java
│       │   │   │           │   │   ├── PullRequestAnalyzedHandler.java
│       │   │   │           │   │   └── README.md
│       │   │   │           │   ├── escalation
│       │   │   │           │   │   ├── EmailNotificationService.java
│       │   │   │           │   │   ├── EscalationService.java
│       │   │   │           │   │   ├── PullRequestStaleEvent.java
│       │   │   │           │   │   ├── ReviewerReassignedEvent.java
│       │   │   │           │   │   └── ReviewReminderSentEvent.java
│       │   │   │           │   └── expertise
│       │   │   │           │       ├── ComplexityCalculatedHandler.java
│       │   │   │           │       ├── ExpertiseBreakdown.java
│       │   │   │           │       ├── ExpertiseCalculatedEvent.java
│       │   │   │           │       ├── ExpertiseCalculator.java
│       │   │   │           │       ├── ExpertiseConfig.java
│       │   │   │           │       ├── ExpertiseService.java
│       │   │   │           │       ├── README.md
│       │   │   │           │       └── ReviewExpertiseEnricher.java
│       │   │   │           ├── entity
│       │   │   │           │   └── BaseEntity.java
│       │   │   │           ├── enums
│       │   │   │           │   ├── AssignmentStatus.java
│       │   │   │           │   ├── ChangeType.java
│       │   │   │           │   ├── DeveloperSeniority.java
│       │   │   │           │   ├── ExpertiseMode.java
│       │   │   │           │   ├── PullRequestStatus.java
│       │   │   │           │   ├── ReviewState.java
│       │   │   │           │   └── ScopeType.java
│       │   │   │           ├── event
│       │   │   │           ├── exception
│       │   │   │           │   ├── GlobalExceptionHandler.java
│       │   │   │           │   ├── InvalidStateException.java
│       │   │   │           │   ├── PrflowException.java
│       │   │   │           │   └── ResourceNotFoundException.java
│       │   │   │           ├── modules
│       │   │   │           │   ├── developer
│       │   │   │           │   │   ├── domain
│       │   │   │           │   │   │   └── Developer.java
│       │   │   │           │   │   └── repository
│       │   │   │           │   │       └── DeveloperRepository.java
│       │   │   │           │   ├── organization
│       │   │   │           │   │   ├── domain
│       │   │   │           │   │   │   └── Organization.java
│       │   │   │           │   │   └── repository
│       │   │   │           │   │       └── OrganizationRepository.java
│       │   │   │           │   ├── pullrequest
│       │   │   │           │   │   ├── controller
│       │   │   │           │   │   │   └── PullRequestEventController.java
│       │   │   │           │   │   ├── domain
│       │   │   │           │   │   │   ├── PullRequestFile.java
│       │   │   │           │   │   │   ├── PullRequest.java
│       │   │   │           │   │   │   └── PullRequestReview.java
│       │   │   │           │   │   ├── event
│       │   │   │           │   │   │   ├── PullRequestMergedEvent.java
│       │   │   │           │   │   │   ├── ReviewsSynchronizedEvent.java
│       │   │   │           │   │   │   ├── ReviewSubmittedEvent.java
│       │   │   │           │   │   │   └── ReviewSyncEventHandler.java
│       │   │   │           │   │   ├── repository
│       │   │   │           │   │   │   ├── PullRequestFileRepository.java
│       │   │   │           │   │   │   ├── PullRequestRepository.java
│       │   │   │           │   │   │   └── PullRequestReviewRepository.java
│       │   │   │           │   │   └── service
│       │   │   │           │   │       ├── GitHubReviewFetcher.java
│       │   │   │           │   │       └── ReviewSyncService.java
│       │   │   │           │   └── repository
│       │   │   │           │       ├── domain
│       │   │   │           │       │   └── Repository.java
│       │   │   │           │       └── repository
│       │   │   │           │           └── RepositoryJpaRepository.java
│       │   │   │           └── SpringBackendApplication.java
│       │   │   └── resources
│       │   │       ├── application.properties
│       │   │       ├── application.yml
│       │   │       ├── db
│       │   │       │   └── migration
│       │   │       │       ├── V10__create_developer_file_expertise.sql
│       │   │       │       ├── V11__create_repository_developers.sql
│       │   │       │       ├── V12__create_reviewer_assignments.sql
│       │   │       │       ├── V13__create_pull_request_reviews.sql
│       │   │       │       ├── V14__add_escalation_fields_to_reviewer_assignments.sql
│       │   │       │       ├── V1__create_organizations.sql
│       │   │       │       ├── V2__create_developers.sql
│       │   │       │       ├── V3__create_repositories.sql
│       │   │       │       ├── V4__create_webhook_logs.sql
│       │   │       │       ├── V5__create_pull_requests.sql
│       │   │       │       ├── V6__create_pull_request_files.sql
│       │   │       │       ├── V7__add_updated_at_to_pull_request_files.sql
│       │   │       │       ├── V8__add_opened_at_index_to_pull_requests.sql
│       │   │       │       └── V9__add_complexity_intelligence_to_pull_requests.sql
│       │   │       ├── static
│       │   │       └── templates
│       │   └── test
│       │       └── java
│       │           └── prflow
│       │               └── spring_backend
│       │                   ├── config
│       │                   │   └── GitHubAuthServiceTest.java
│       │                   ├── engines
│       │                   │   ├── assignment
│       │                   │   │   ├── AssignmentScoringServiceTest.java
│       │                   │   │   └── AssignmentServiceTest.java
│       │                   │   ├── complexity
│       │                   │   │   └── ComplexityCalculatorTest.java
│       │                   │   └── escalation
│       │                   │       └── EscalationServiceTest.java
│       │                   ├── modules
│       │                   │   └── pullrequest
│       │                   │       └── service
│       │                   │           └── ReviewSyncServiceTest.java
│       │                   └── SpringBackendApplicationTests.java
│       └── target
│           ├── classes
│           │   ├── application.properties
│           │   ├── application.yml
│           │   ├── db
│           │   │   └── migration
│           │   │       ├── V10__create_developer_file_expertise.sql
│           │   │       ├── V11__create_repository_developers.sql
│           │   │       ├── V12__create_reviewer_assignments.sql
│           │   │       ├── V13__create_pull_request_reviews.sql
│           │   │       ├── V14__add_escalation_fields_to_reviewer_assignments.sql
│           │   │       ├── V1__create_organizations.sql
│           │   │       ├── V2__create_developers.sql
│           │   │       ├── V3__create_repositories.sql
│           │   │       ├── V4__create_webhook_logs.sql
│           │   │       ├── V5__create_pull_requests.sql
│           │   │       ├── V6__create_pull_request_files.sql
│           │   │       ├── V7__add_updated_at_to_pull_request_files.sql
│           │   │       ├── V8__add_opened_at_index_to_pull_requests.sql
│           │   │       └── V9__add_complexity_intelligence_to_pull_requests.sql
│           │   └── prflow
│           │       └── spring_backend
│           │           ├── common
│           │           ├── config
│           │           │   ├── GitHubAuthService$CachedToken.class
│           │           │   ├── GitHubAuthService.class
│           │           │   ├── JpaAuditingConfig.class
│           │           │   └── SecurityConfig.class
│           │           ├── dto
│           │           │   └── ApiResponse.class
│           │           ├── engines
│           │           │   ├── assignment
│           │           │   │   ├── AssignmentConfig.class
│           │           │   │   ├── AssignmentEventHandler.class
│           │           │   │   ├── AssignmentScoringService.class
│           │           │   │   ├── AssignmentService$DeveloperMeta.class
│           │           │   │   ├── AssignmentService$PullRequestState.class
│           │           │   │   ├── AssignmentService.class
│           │           │   │   ├── dto
│           │           │   │   │   └── ReviewerAssignmentDto.class
│           │           │   │   ├── model
│           │           │   │   │   └── ReviewerCandidate.class
│           │           │   │   └── ReviewersAssignedEvent.class
│           │           │   ├── complexity
│           │           │   │   ├── ComplexityBreakdown.class
│           │           │   │   ├── ComplexityCalculatedEvent.class
│           │           │   │   ├── ComplexityCalculator.class
│           │           │   │   ├── ComplexityConfig$Normalization.class
│           │           │   │   ├── ComplexityConfig$Weights.class
│           │           │   │   ├── ComplexityConfig.class
│           │           │   │   ├── ComplexityLevel.class
│           │           │   │   ├── ComplexityService$PullRequestState.class
│           │           │   │   ├── ComplexityService$PullRequestStateMapper.class
│           │           │   │   ├── ComplexityService$SignalAggregate.class
│           │           │   │   ├── ComplexityService$SignalAggregateMapper.class
│           │           │   │   ├── ComplexityService.class
│           │           │   │   ├── PullRequestAnalyzedHandler$PullRequestAnalyzedEvent.class
│           │           │   │   ├── PullRequestAnalyzedHandler.class
│           │           │   │   └── README.md
│           │           │   ├── escalation
│           │           │   │   ├── EmailNotificationService.class
│           │           │   │   ├── EscalationService$AssignmentEscalationMeta.class
│           │           │   │   ├── EscalationService$NewReviewerMeta.class
│           │           │   │   ├── EscalationService.class
│           │           │   │   ├── PullRequestStaleEvent.class
│           │           │   │   ├── ReviewerReassignedEvent.class
│           │           │   │   └── ReviewReminderSentEvent.class
│           │           │   └── expertise
│           │           │       ├── ComplexityCalculatedHandler.class
│           │           │       ├── ExpertiseBreakdown.class
│           │           │       ├── ExpertiseCalculatedEvent.class
│           │           │       ├── ExpertiseCalculator.class
│           │           │       ├── ExpertiseConfig$Decay.class
│           │           │       ├── ExpertiseConfig$Normalization.class
│           │           │       ├── ExpertiseConfig$Weights.class
│           │           │       ├── ExpertiseConfig.class
│           │           │       ├── ExpertiseService$PullRequestFileMeta.class
│           │           │       ├── ExpertiseService$PullRequestMeta.class
│           │           │       ├── ExpertiseService.class
│           │           │       ├── README.md
│           │           │       ├── ReviewExpertiseEnricher$PullRequestFileMeta.class
│           │           │       └── ReviewExpertiseEnricher.class
│           │           ├── entity
│           │           │   └── BaseEntity.class
│           │           ├── enums
│           │           │   ├── AssignmentStatus.class
│           │           │   ├── ChangeType.class
│           │           │   ├── DeveloperSeniority.class
│           │           │   ├── ExpertiseMode.class
│           │           │   ├── PullRequestStatus.class
│           │           │   ├── ReviewState.class
│           │           │   └── ScopeType.class
│           │           ├── event
│           │           ├── exception
│           │           │   ├── GlobalExceptionHandler.class
│           │           │   ├── InvalidStateException.class
│           │           │   ├── PrflowException.class
│           │           │   └── ResourceNotFoundException.class
│           │           ├── modules
│           │           │   ├── developer
│           │           │   │   ├── domain
│           │           │   │   │   └── Developer.class
│           │           │   │   └── repository
│           │           │   │       └── DeveloperRepository.class
│           │           │   ├── organization
│           │           │   │   ├── domain
│           │           │   │   │   └── Organization.class
│           │           │   │   └── repository
│           │           │   │       └── OrganizationRepository.class
│           │           │   ├── pullrequest
│           │           │   │   ├── controller
│           │           │   │   │   ├── PullRequestEventController$EventRequest.class
│           │           │   │   │   └── PullRequestEventController.class
│           │           │   │   ├── domain
│           │           │   │   │   ├── PullRequest.class
│           │           │   │   │   ├── PullRequestFile.class
│           │           │   │   │   └── PullRequestReview.class
│           │           │   │   ├── event
│           │           │   │   │   ├── PullRequestMergedEvent.class
│           │           │   │   │   ├── ReviewsSynchronizedEvent.class
│           │           │   │   │   ├── ReviewSubmittedEvent.class
│           │           │   │   │   └── ReviewSyncEventHandler.class
│           │           │   │   ├── repository
│           │           │   │   │   ├── PullRequestFileRepository.class
│           │           │   │   │   ├── PullRequestRepository.class
│           │           │   │   │   └── PullRequestReviewRepository.class
│           │           │   │   └── service
│           │           │   │       ├── GitHubReviewFetcher$1.class
│           │           │   │       ├── GitHubReviewFetcher$GitHubReviewDto.class
│           │           │   │       ├── GitHubReviewFetcher$GitHubUserDto.class
│           │           │   │       ├── GitHubReviewFetcher.class
│           │           │   │       ├── ReviewSyncService$PullRequestSyncMeta.class
│           │           │   │       └── ReviewSyncService.class
│           │           │   └── repository
│           │           │       ├── domain
│           │           │       │   └── Repository.class
│           │           │       └── repository
│           │           │           └── RepositoryJpaRepository.class
│           │           └── SpringBackendApplication.class
│           ├── generated-sources
│           │   └── annotations
│           ├── generated-test-sources
│           │   └── test-annotations
│           ├── maven-status
│           │   └── maven-compiler-plugin
│           │       ├── compile
│           │       │   └── default-compile
│           │       │       ├── createdFiles.lst
│           │       │       └── inputFiles.lst
│           │       └── testCompile
│           │           └── default-testCompile
│           │               ├── createdFiles.lst
│           │               └── inputFiles.lst
│           ├── surefire-reports
│           │   ├── prflow.spring_backend.config.GitHubAuthServiceTest.txt
│           │   ├── prflow.spring_backend.engines.assignment.AssignmentScoringServiceTest.txt
│           │   ├── prflow.spring_backend.engines.assignment.AssignmentServiceTest.txt
│           │   ├── prflow.spring_backend.engines.complexity.ComplexityCalculatorTest.txt
│           │   ├── prflow.spring_backend.engines.escalation.EscalationServiceTest.txt
│           │   ├── prflow.spring_backend.modules.pullrequest.service.ReviewSyncServiceTest.txt
│           │   ├── prflow.spring_backend.SpringBackendApplicationTests.txt
│           │   ├── TEST-prflow.spring_backend.config.GitHubAuthServiceTest.xml
│           │   ├── TEST-prflow.spring_backend.engines.assignment.AssignmentScoringServiceTest.xml
│           │   ├── TEST-prflow.spring_backend.engines.assignment.AssignmentServiceTest.xml
│           │   ├── TEST-prflow.spring_backend.engines.complexity.ComplexityCalculatorTest.xml
│           │   ├── TEST-prflow.spring_backend.engines.escalation.EscalationServiceTest.xml
│           │   ├── TEST-prflow.spring_backend.modules.pullrequest.service.ReviewSyncServiceTest.xml
│           │   └── TEST-prflow.spring_backend.SpringBackendApplicationTests.xml
│           └── test-classes
│               └── prflow
│                   └── spring_backend
│                       ├── config
│                       │   └── GitHubAuthServiceTest.class
│                       ├── engines
│                       │   ├── assignment
│                       │   │   ├── AssignmentScoringServiceTest.class
│                       │   │   ├── AssignmentServiceTest$1.class
│                       │   │   └── AssignmentServiceTest.class
│                       │   ├── complexity
│                       │   │   └── ComplexityCalculatorTest.class
│                       │   └── escalation
│                       │       └── EscalationServiceTest.class
│                       ├── modules
│                       │   └── pullrequest
│                       │       └── service
│                       │           └── ReviewSyncServiceTest.class
│                       └── SpringBackendApplicationTests.class
├── docs
│   ├── analysis
│   │   ├── database-architecture.md
│   │   ├── engine-architecture.md
│   │   ├── event-orchestration.md
│   │   ├── github-integration.md
│   │   └── system-evolution-and-gaps.md
│   ├── architecture
│   │   └── contributor-synchronization.md
│   ├── database
│   │   ├── postgres-lifecycle.md
│   │   └── workflow-persistence-step1.md
│   ├── engines
│   │   ├── complexity-engine-implementation-guide.md
│   │   ├── escalation-engine-guide.md
│   │   └── review-intelligence-foundation.md
│   ├── events
│   │   ├── complexity-engine-v1.md
│   │   ├── github-app-setup.md
│   │   ├── github-pr-files-client-step3.md
│   │   ├── pr-persistence-orchestration-step4.md
│   │   └── pr-persistence-pipeline.md
│   └── workflows
├── frontend
│   └── dashboard
├── infra
│   ├── docker
│   │   └── docker-compose.yml
│   └── scripts
├── integrations
│   └── github-webhook-service
│       ├── bun.lock
│       ├── CLAUDE.md
│       ├── index.ts
│       ├── node_modules
│       │   ├── accepts
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── acorn
│       │   │   ├── bin
│       │   │   │   └── acorn
│       │   │   ├── CHANGELOG.md
│       │   │   ├── dist
│       │   │   │   ├── acorn.d.mts
│       │   │   │   ├── acorn.d.ts
│       │   │   │   ├── acorn.js
│       │   │   │   ├── acorn.mjs
│       │   │   │   └── bin.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── acorn-walk
│       │   │   ├── CHANGELOG.md
│       │   │   ├── dist
│       │   │   │   ├── walk.d.mts
│       │   │   │   ├── walk.d.ts
│       │   │   │   ├── walk.js
│       │   │   │   └── walk.mjs
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── anymatch
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── arg
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE.md
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── balanced-match
│       │   │   ├── dist
│       │   │   │   ├── commonjs
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── index.d.ts.map
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── index.js.map
│       │   │   │   │   └── package.json
│       │   │   │   └── esm
│       │   │   │       ├── index.d.ts
│       │   │   │       ├── index.d.ts.map
│       │   │   │       ├── index.js
│       │   │   │       ├── index.js.map
│       │   │   │       └── package.json
│       │   │   ├── LICENSE.md
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── before-after-hook
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── add.js
│       │   │   │   ├── register.js
│       │   │   │   └── remove.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── binary-extensions
│       │   │   ├── binary-extensions.json
│       │   │   ├── binary-extensions.json.d.ts
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── license
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── body-parser
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── read.js
│       │   │   │   ├── types
│       │   │   │   │   ├── json.js
│       │   │   │   │   ├── raw.js
│       │   │   │   │   ├── text.js
│       │   │   │   │   └── urlencoded.js
│       │   │   │   └── utils.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── brace-expansion
│       │   │   ├── dist
│       │   │   │   ├── commonjs
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── index.d.ts.map
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── index.js.map
│       │   │   │   │   └── package.json
│       │   │   │   └── esm
│       │   │   │       ├── index.d.ts
│       │   │   │       ├── index.d.ts.map
│       │   │   │       ├── index.js
│       │   │   │       ├── index.js.map
│       │   │   │       └── package.json
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── braces
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── compile.js
│       │   │   │   ├── constants.js
│       │   │   │   ├── expand.js
│       │   │   │   ├── parse.js
│       │   │   │   ├── stringify.js
│       │   │   │   └── utils.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── bun-types
│       │   │   ├── bundle.d.ts
│       │   │   ├── bun.d.ts
│       │   │   ├── bun.ns.d.ts
│       │   │   ├── deprecated.d.ts
│       │   │   ├── devserver.d.ts
│       │   │   ├── docs
│       │   │   │   ├── bundler
│       │   │   │   │   ├── bytecode.mdx
│       │   │   │   │   ├── css.mdx
│       │   │   │   │   ├── esbuild.mdx
│       │   │   │   │   ├── executables.mdx
│       │   │   │   │   ├── fullstack.mdx
│       │   │   │   │   ├── hot-reloading.mdx
│       │   │   │   │   ├── html-static.mdx
│       │   │   │   │   ├── index.mdx
│       │   │   │   │   ├── loaders.mdx
│       │   │   │   │   ├── macros.mdx
│       │   │   │   │   ├── minifier.mdx
│       │   │   │   │   ├── plugins.mdx
│       │   │   │   │   └── standalone-html.mdx
│       │   │   │   ├── feedback.mdx
│       │   │   │   ├── guides
│       │   │   │   │   ├── binary
│       │   │   │   │   │   ├── arraybuffer-to-array.mdx
│       │   │   │   │   │   ├── arraybuffer-to-blob.mdx
│       │   │   │   │   │   ├── arraybuffer-to-buffer.mdx
│       │   │   │   │   │   ├── arraybuffer-to-string.mdx
│       │   │   │   │   │   ├── arraybuffer-to-typedarray.mdx
│       │   │   │   │   │   ├── blob-to-arraybuffer.mdx
│       │   │   │   │   │   ├── blob-to-dataview.mdx
│       │   │   │   │   │   ├── blob-to-stream.mdx
│       │   │   │   │   │   ├── blob-to-string.mdx
│       │   │   │   │   │   ├── blob-to-typedarray.mdx
│       │   │   │   │   │   ├── buffer-to-arraybuffer.mdx
│       │   │   │   │   │   ├── buffer-to-blob.mdx
│       │   │   │   │   │   ├── buffer-to-readablestream.mdx
│       │   │   │   │   │   ├── buffer-to-string.mdx
│       │   │   │   │   │   ├── buffer-to-typedarray.mdx
│       │   │   │   │   │   ├── dataview-to-string.mdx
│       │   │   │   │   │   ├── typedarray-to-arraybuffer.mdx
│       │   │   │   │   │   ├── typedarray-to-blob.mdx
│       │   │   │   │   │   ├── typedarray-to-buffer.mdx
│       │   │   │   │   │   ├── typedarray-to-dataview.mdx
│       │   │   │   │   │   ├── typedarray-to-readablestream.mdx
│       │   │   │   │   │   └── typedarray-to-string.mdx
│       │   │   │   │   ├── deployment
│       │   │   │   │   │   ├── aws-lambda.mdx
│       │   │   │   │   │   ├── digital-ocean.mdx
│       │   │   │   │   │   ├── google-cloud-run.mdx
│       │   │   │   │   │   ├── railway.mdx
│       │   │   │   │   │   ├── render.mdx
│       │   │   │   │   │   └── vercel.mdx
│       │   │   │   │   ├── ecosystem
│       │   │   │   │   │   ├── astro.mdx
│       │   │   │   │   │   ├── discordjs.mdx
│       │   │   │   │   │   ├── docker.mdx
│       │   │   │   │   │   ├── drizzle.mdx
│       │   │   │   │   │   ├── elysia.mdx
│       │   │   │   │   │   ├── express.mdx
│       │   │   │   │   │   ├── gel.mdx
│       │   │   │   │   │   ├── hono.mdx
│       │   │   │   │   │   ├── mongoose.mdx
│       │   │   │   │   │   ├── neon-drizzle.mdx
│       │   │   │   │   │   ├── neon-serverless-postgres.mdx
│       │   │   │   │   │   ├── nextjs.mdx
│       │   │   │   │   │   ├── nuxt.mdx
│       │   │   │   │   │   ├── pm2.mdx
│       │   │   │   │   │   ├── prisma.mdx
│       │   │   │   │   │   ├── prisma-postgres.mdx
│       │   │   │   │   │   ├── qwik.mdx
│       │   │   │   │   │   ├── react.mdx
│       │   │   │   │   │   ├── remix.mdx
│       │   │   │   │   │   ├── sentry.mdx
│       │   │   │   │   │   ├── solidstart.mdx
│       │   │   │   │   │   ├── ssr-react.mdx
│       │   │   │   │   │   ├── stric.mdx
│       │   │   │   │   │   ├── sveltekit.mdx
│       │   │   │   │   │   ├── systemd.mdx
│       │   │   │   │   │   ├── tanstack-start.mdx
│       │   │   │   │   │   ├── upstash.mdx
│       │   │   │   │   │   └── vite.mdx
│       │   │   │   │   ├── html-rewriter
│       │   │   │   │   │   ├── extract-links.mdx
│       │   │   │   │   │   └── extract-social-meta.mdx
│       │   │   │   │   ├── http
│       │   │   │   │   │   ├── cluster.mdx
│       │   │   │   │   │   ├── fetch.mdx
│       │   │   │   │   │   ├── fetch-unix.mdx
│       │   │   │   │   │   ├── file-uploads.mdx
│       │   │   │   │   │   ├── hot.mdx
│       │   │   │   │   │   ├── proxy.mdx
│       │   │   │   │   │   ├── server.mdx
│       │   │   │   │   │   ├── simple.mdx
│       │   │   │   │   │   ├── sse.mdx
│       │   │   │   │   │   ├── stream-file.mdx
│       │   │   │   │   │   ├── stream-iterator.mdx
│       │   │   │   │   │   ├── stream-node-streams-in-bun.mdx
│       │   │   │   │   │   └── tls.mdx
│       │   │   │   │   ├── index.mdx
│       │   │   │   │   ├── install
│       │   │   │   │   │   ├── add-dev.mdx
│       │   │   │   │   │   ├── add-git.mdx
│       │   │   │   │   │   ├── add.mdx
│       │   │   │   │   │   ├── add-optional.mdx
│       │   │   │   │   │   ├── add-peer.mdx
│       │   │   │   │   │   ├── add-tarball.mdx
│       │   │   │   │   │   ├── azure-artifacts.mdx
│       │   │   │   │   │   ├── cicd.mdx
│       │   │   │   │   │   ├── custom-registry.mdx
│       │   │   │   │   │   ├── from-npm-install-to-bun-install.mdx
│       │   │   │   │   │   ├── git-diff-bun-lockfile.mdx
│       │   │   │   │   │   ├── jfrog-artifactory.mdx
│       │   │   │   │   │   ├── npm-alias.mdx
│       │   │   │   │   │   ├── registry-scope.mdx
│       │   │   │   │   │   ├── trusted.mdx
│       │   │   │   │   │   ├── workspaces.mdx
│       │   │   │   │   │   └── yarnlock.mdx
│       │   │   │   │   ├── process
│       │   │   │   │   │   ├── argv.mdx
│       │   │   │   │   │   ├── ctrl-c.mdx
│       │   │   │   │   │   ├── ipc.mdx
│       │   │   │   │   │   ├── nanoseconds.mdx
│       │   │   │   │   │   ├── os-signals.mdx
│       │   │   │   │   │   ├── spawn.mdx
│       │   │   │   │   │   ├── spawn-stderr.mdx
│       │   │   │   │   │   ├── spawn-stdout.mdx
│       │   │   │   │   │   └── stdin.mdx
│       │   │   │   │   ├── read-file
│       │   │   │   │   │   ├── arraybuffer.mdx
│       │   │   │   │   │   ├── buffer.mdx
│       │   │   │   │   │   ├── exists.mdx
│       │   │   │   │   │   ├── json.mdx
│       │   │   │   │   │   ├── mime.mdx
│       │   │   │   │   │   ├── stream.mdx
│       │   │   │   │   │   ├── string.mdx
│       │   │   │   │   │   ├── uint8array.mdx
│       │   │   │   │   │   └── watch.mdx
│       │   │   │   │   ├── runtime
│       │   │   │   │   │   ├── build-time-constants.mdx
│       │   │   │   │   │   ├── cicd.mdx
│       │   │   │   │   │   ├── codesign-macos-executable.mdx
│       │   │   │   │   │   ├── define-constant.mdx
│       │   │   │   │   │   ├── delete-directory.mdx
│       │   │   │   │   │   ├── delete-file.mdx
│       │   │   │   │   │   ├── heap-snapshot.mdx
│       │   │   │   │   │   ├── import-html.mdx
│       │   │   │   │   │   ├── import-json5.mdx
│       │   │   │   │   │   ├── import-json.mdx
│       │   │   │   │   │   ├── import-toml.mdx
│       │   │   │   │   │   ├── import-yaml.mdx
│       │   │   │   │   │   ├── read-env.mdx
│       │   │   │   │   │   ├── set-env.mdx
│       │   │   │   │   │   ├── shell.mdx
│       │   │   │   │   │   ├── timezone.mdx
│       │   │   │   │   │   ├── tsconfig-paths.mdx
│       │   │   │   │   │   ├── typescript.mdx
│       │   │   │   │   │   ├── vscode-debugger.mdx
│       │   │   │   │   │   └── web-debugger.mdx
│       │   │   │   │   ├── streams
│       │   │   │   │   │   ├── node-readable-to-arraybuffer.mdx
│       │   │   │   │   │   ├── node-readable-to-blob.mdx
│       │   │   │   │   │   ├── node-readable-to-json.mdx
│       │   │   │   │   │   ├── node-readable-to-string.mdx
│       │   │   │   │   │   ├── node-readable-to-uint8array.mdx
│       │   │   │   │   │   ├── to-arraybuffer.mdx
│       │   │   │   │   │   ├── to-array.mdx
│       │   │   │   │   │   ├── to-blob.mdx
│       │   │   │   │   │   ├── to-buffer.mdx
│       │   │   │   │   │   ├── to-json.mdx
│       │   │   │   │   │   ├── to-string.mdx
│       │   │   │   │   │   └── to-typedarray.mdx
│       │   │   │   │   ├── test
│       │   │   │   │   │   ├── bail.mdx
│       │   │   │   │   │   ├── concurrent-test-glob.mdx
│       │   │   │   │   │   ├── coverage.mdx
│       │   │   │   │   │   ├── coverage-threshold.mdx
│       │   │   │   │   │   ├── happy-dom.mdx
│       │   │   │   │   │   ├── migrate-from-jest.mdx
│       │   │   │   │   │   ├── mock-clock.mdx
│       │   │   │   │   │   ├── mock-functions.mdx
│       │   │   │   │   │   ├── rerun-each.mdx
│       │   │   │   │   │   ├── run-tests.mdx
│       │   │   │   │   │   ├── skip-tests.mdx
│       │   │   │   │   │   ├── snapshot.mdx
│       │   │   │   │   │   ├── spy-on.mdx
│       │   │   │   │   │   ├── svelte-test.mdx
│       │   │   │   │   │   ├── testing-library.mdx
│       │   │   │   │   │   ├── timeout.mdx
│       │   │   │   │   │   ├── todo-tests.mdx
│       │   │   │   │   │   ├── update-snapshots.mdx
│       │   │   │   │   │   └── watch-mode.mdx
│       │   │   │   │   ├── util
│       │   │   │   │   │   ├── base64.mdx
│       │   │   │   │   │   ├── deep-equals.mdx
│       │   │   │   │   │   ├── deflate.mdx
│       │   │   │   │   │   ├── detect-bun.mdx
│       │   │   │   │   │   ├── entrypoint.mdx
│       │   │   │   │   │   ├── escape-html.mdx
│       │   │   │   │   │   ├── file-url-to-path.mdx
│       │   │   │   │   │   ├── gzip.mdx
│       │   │   │   │   │   ├── hash-a-password.mdx
│       │   │   │   │   │   ├── import-meta-dir.mdx
│       │   │   │   │   │   ├── import-meta-file.mdx
│       │   │   │   │   │   ├── import-meta-path.mdx
│       │   │   │   │   │   ├── javascript-uuid.mdx
│       │   │   │   │   │   ├── main.mdx
│       │   │   │   │   │   ├── path-to-file-url.mdx
│       │   │   │   │   │   ├── sleep.mdx
│       │   │   │   │   │   ├── upgrade.mdx
│       │   │   │   │   │   ├── version.mdx
│       │   │   │   │   │   └── which-path-to-executable-bin.mdx
│       │   │   │   │   ├── websocket
│       │   │   │   │   │   ├── compression.mdx
│       │   │   │   │   │   ├── context.mdx
│       │   │   │   │   │   ├── pubsub.mdx
│       │   │   │   │   │   └── simple.mdx
│       │   │   │   │   └── write-file
│       │   │   │   │       ├── append.mdx
│       │   │   │   │       ├── basic.mdx
│       │   │   │   │       ├── blob.mdx
│       │   │   │   │       ├── cat.mdx
│       │   │   │   │       ├── file-cp.mdx
│       │   │   │   │       ├── filesink.mdx
│       │   │   │   │       ├── response.mdx
│       │   │   │   │       ├── stdout.mdx
│       │   │   │   │       ├── stream.mdx
│       │   │   │   │       └── unlink.mdx
│       │   │   │   ├── index.mdx
│       │   │   │   ├── installation.mdx
│       │   │   │   ├── pm
│       │   │   │   │   ├── bunx.mdx
│       │   │   │   │   ├── catalogs.mdx
│       │   │   │   │   ├── cli
│       │   │   │   │   │   ├── add.mdx
│       │   │   │   │   │   ├── audit.mdx
│       │   │   │   │   │   ├── info.mdx
│       │   │   │   │   │   ├── install.mdx
│       │   │   │   │   │   ├── link.mdx
│       │   │   │   │   │   ├── outdated.mdx
│       │   │   │   │   │   ├── patch.mdx
│       │   │   │   │   │   ├── pm.mdx
│       │   │   │   │   │   ├── publish.mdx
│       │   │   │   │   │   ├── remove.mdx
│       │   │   │   │   │   ├── update.mdx
│       │   │   │   │   │   └── why.mdx
│       │   │   │   │   ├── filter.mdx
│       │   │   │   │   ├── global-cache.mdx
│       │   │   │   │   ├── global-store.mdx
│       │   │   │   │   ├── isolated-installs.mdx
│       │   │   │   │   ├── lifecycle.mdx
│       │   │   │   │   ├── lockfile.mdx
│       │   │   │   │   ├── npmrc.mdx
│       │   │   │   │   ├── overrides.mdx
│       │   │   │   │   ├── scopes-registries.mdx
│       │   │   │   │   ├── security-scanner-api.mdx
│       │   │   │   │   └── workspaces.mdx
│       │   │   │   ├── project
│       │   │   │   │   ├── benchmarking.mdx
│       │   │   │   │   ├── bindgen.mdx
│       │   │   │   │   ├── building-windows.mdx
│       │   │   │   │   ├── contributing.mdx
│       │   │   │   │   ├── feedback.mdx
│       │   │   │   │   ├── license.mdx
│       │   │   │   │   └── roadmap.mdx
│       │   │   │   ├── quickstart.mdx
│       │   │   │   ├── README.md
│       │   │   │   ├── runtime
│       │   │   │   │   ├── archive.mdx
│       │   │   │   │   ├── auto-install.mdx
│       │   │   │   │   ├── binary-data.mdx
│       │   │   │   │   ├── bun-apis.mdx
│       │   │   │   │   ├── bunfig.mdx
│       │   │   │   │   ├── c-compiler.mdx
│       │   │   │   │   ├── child-process.mdx
│       │   │   │   │   ├── color.mdx
│       │   │   │   │   ├── console.mdx
│       │   │   │   │   ├── cookies.mdx
│       │   │   │   │   ├── cron.mdx
│       │   │   │   │   ├── csrf.mdx
│       │   │   │   │   ├── debugger.mdx
│       │   │   │   │   ├── environment-variables.mdx
│       │   │   │   │   ├── ffi.mdx
│       │   │   │   │   ├── file-io.mdx
│       │   │   │   │   ├── file-system-router.mdx
│       │   │   │   │   ├── file-types.mdx
│       │   │   │   │   ├── globals.mdx
│       │   │   │   │   ├── glob.mdx
│       │   │   │   │   ├── hashing.mdx
│       │   │   │   │   ├── html-rewriter.mdx
│       │   │   │   │   ├── http
│       │   │   │   │   │   ├── cookies.mdx
│       │   │   │   │   │   ├── error-handling.mdx
│       │   │   │   │   │   ├── metrics.mdx
│       │   │   │   │   │   ├── routing.mdx
│       │   │   │   │   │   ├── server.mdx
│       │   │   │   │   │   ├── tls.mdx
│       │   │   │   │   │   └── websockets.mdx
│       │   │   │   │   ├── image.mdx
│       │   │   │   │   ├── index.mdx
│       │   │   │   │   ├── json5.mdx
│       │   │   │   │   ├── jsonl.mdx
│       │   │   │   │   ├── jsx.mdx
│       │   │   │   │   ├── markdown.mdx
│       │   │   │   │   ├── module-resolution.mdx
│       │   │   │   │   ├── networking
│       │   │   │   │   │   ├── dns.mdx
│       │   │   │   │   │   ├── fetch.mdx
│       │   │   │   │   │   ├── tcp.mdx
│       │   │   │   │   │   └── udp.mdx
│       │   │   │   │   ├── node-api.mdx
│       │   │   │   │   ├── nodejs-compat.mdx
│       │   │   │   │   ├── plugins.mdx
│       │   │   │   │   ├── redis.mdx
│       │   │   │   │   ├── repl.mdx
│       │   │   │   │   ├── s3.mdx
│       │   │   │   │   ├── secrets.mdx
│       │   │   │   │   ├── semver.mdx
│       │   │   │   │   ├── shell.mdx
│       │   │   │   │   ├── sqlite.mdx
│       │   │   │   │   ├── sql.mdx
│       │   │   │   │   ├── streams.mdx
│       │   │   │   │   ├── templating
│       │   │   │   │   │   ├── create.mdx
│       │   │   │   │   │   └── init.mdx
│       │   │   │   │   ├── toml.mdx
│       │   │   │   │   ├── transpiler.mdx
│       │   │   │   │   ├── typescript.mdx
│       │   │   │   │   ├── utils.mdx
│       │   │   │   │   ├── watch-mode.mdx
│       │   │   │   │   ├── web-apis.mdx
│       │   │   │   │   ├── webview.mdx
│       │   │   │   │   ├── workers.mdx
│       │   │   │   │   └── yaml.mdx
│       │   │   │   ├── snippets
│       │   │   │   │   └── cli
│       │   │   │   │       ├── add.mdx
│       │   │   │   │       ├── build.mdx
│       │   │   │   │       ├── bunx.mdx
│       │   │   │   │       ├── feedback.mdx
│       │   │   │   │       ├── init.mdx
│       │   │   │   │       ├── install.mdx
│       │   │   │   │       ├── link.mdx
│       │   │   │   │       ├── outdated.mdx
│       │   │   │   │       ├── patch.mdx
│       │   │   │   │       ├── publish.mdx
│       │   │   │   │       ├── remove.mdx
│       │   │   │   │       ├── run.mdx
│       │   │   │   │       ├── test.mdx
│       │   │   │   │       └── update.mdx
│       │   │   │   ├── test
│       │   │   │   │   ├── code-coverage.mdx
│       │   │   │   │   ├── configuration.mdx
│       │   │   │   │   ├── dates-times.mdx
│       │   │   │   │   ├── discovery.mdx
│       │   │   │   │   ├── dom.mdx
│       │   │   │   │   ├── index.mdx
│       │   │   │   │   ├── lifecycle.mdx
│       │   │   │   │   ├── mocks.mdx
│       │   │   │   │   ├── reporters.mdx
│       │   │   │   │   ├── runtime-behavior.mdx
│       │   │   │   │   ├── snapshots.mdx
│       │   │   │   │   └── writing-tests.mdx
│       │   │   │   ├── typescript-6.mdx
│       │   │   │   └── typescript.mdx
│       │   │   ├── extensions.d.ts
│       │   │   ├── fetch.d.ts
│       │   │   ├── ffi.d.ts
│       │   │   ├── globals.d.ts
│       │   │   ├── html-rewriter.d.ts
│       │   │   ├── index.d.ts
│       │   │   ├── jsc.d.ts
│       │   │   ├── jsx.d.ts
│       │   │   ├── overrides.d.ts
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── redis.d.ts
│       │   │   ├── s3.d.ts
│       │   │   ├── security.d.ts
│       │   │   ├── serve.d.ts
│       │   │   ├── shell.d.ts
│       │   │   ├── sql.d.ts
│       │   │   ├── sqlite.d.ts
│       │   │   ├── test.d.ts
│       │   │   ├── test-globals.d.ts
│       │   │   ├── vendor
│       │   │   │   └── expect-type
│       │   │   │       ├── branding.d.ts
│       │   │   │       ├── index.d.ts
│       │   │   │       ├── messages.d.ts
│       │   │   │       ├── overloads.d.ts
│       │   │   │       └── utils.d.ts
│       │   │   └── wasm.d.ts
│       │   ├── bytes
│       │   │   ├── History.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── Readme.md
│       │   ├── call-bind-apply-helpers
│       │   │   ├── actualApply.d.ts
│       │   │   ├── actualApply.js
│       │   │   ├── applyBind.d.ts
│       │   │   ├── applyBind.js
│       │   │   ├── CHANGELOG.md
│       │   │   ├── functionApply.d.ts
│       │   │   ├── functionApply.js
│       │   │   ├── functionCall.d.ts
│       │   │   ├── functionCall.js
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── reflectApply.d.ts
│       │   │   ├── reflectApply.js
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── call-bound
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── chokidar
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── constants.js
│       │   │   │   ├── fsevents-handler.js
│       │   │   │   └── nodefs-handler.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── types
│       │   │       └── index.d.ts
│       │   ├── content-disposition
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── content-type
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── cookie
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── SECURITY.md
│       │   ├── cookie-signature
│       │   │   ├── History.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── Readme.md
│       │   ├── create-require
│       │   │   ├── CHANGELOG.md
│       │   │   ├── create-require.d.ts
│       │   │   ├── create-require.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── crypto
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── @cspotcode
│       │   │   └── source-map-support
│       │   │       ├── browser-source-map-support.js
│       │   │       ├── LICENSE.md
│       │   │       ├── package.json
│       │   │       ├── README.md
│       │   │       ├── register.d.ts
│       │   │       ├── register-hook-require.d.ts
│       │   │       ├── register-hook-require.js
│       │   │       ├── register.js
│       │   │       ├── source-map-support.d.ts
│       │   │       └── source-map-support.js
│       │   ├── debug
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── src
│       │   │       ├── browser.js
│       │   │       ├── common.js
│       │   │       ├── index.js
│       │   │       └── node.js
│       │   ├── depd
│       │   │   ├── History.md
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   └── browser
│       │   │   │       └── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── Readme.md
│       │   ├── diff
│       │   │   ├── CONTRIBUTING.md
│       │   │   ├── dist
│       │   │   │   ├── diff.js
│       │   │   │   └── diff.min.js
│       │   │   ├── lib
│       │   │   │   ├── convert
│       │   │   │   │   ├── dmp.js
│       │   │   │   │   └── xml.js
│       │   │   │   ├── diff
│       │   │   │   │   ├── array.js
│       │   │   │   │   ├── base.js
│       │   │   │   │   ├── character.js
│       │   │   │   │   ├── css.js
│       │   │   │   │   ├── json.js
│       │   │   │   │   ├── line.js
│       │   │   │   │   ├── sentence.js
│       │   │   │   │   └── word.js
│       │   │   │   ├── index.es6.js
│       │   │   │   ├── index.js
│       │   │   │   ├── patch
│       │   │   │   │   ├── apply.js
│       │   │   │   │   ├── create.js
│       │   │   │   │   ├── merge.js
│       │   │   │   │   └── parse.js
│       │   │   │   └── util
│       │   │   │       ├── array.js
│       │   │   │       ├── distance-iterator.js
│       │   │   │       └── params.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── release-notes.md
│       │   │   └── runtime.js
│       │   ├── dotenv
│       │   │   ├── CHANGELOG.md
│       │   │   ├── config.d.ts
│       │   │   ├── config.js
│       │   │   ├── lib
│       │   │   │   ├── cli-options.js
│       │   │   │   ├── env-options.js
│       │   │   │   ├── main.d.ts
│       │   │   │   └── main.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README-es.md
│       │   │   ├── README.md
│       │   │   ├── SECURITY.md
│       │   │   └── skills
│       │   │       ├── dotenv
│       │   │       │   └── SKILL.md
│       │   │       └── dotenvx
│       │   │           └── SKILL.md
│       │   ├── dunder-proto
│       │   │   ├── CHANGELOG.md
│       │   │   ├── get.d.ts
│       │   │   ├── get.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── set.d.ts
│       │   │   ├── set.js
│       │   │   ├── test
│       │   │   │   ├── get.js
│       │   │   │   ├── index.js
│       │   │   │   └── set.js
│       │   │   └── tsconfig.json
│       │   ├── ee-first
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── encodeurl
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── escape-html
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── Readme.md
│       │   ├── es-define-property
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── es-errors
│       │   │   ├── CHANGELOG.md
│       │   │   ├── eval.d.ts
│       │   │   ├── eval.js
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── range.d.ts
│       │   │   ├── range.js
│       │   │   ├── README.md
│       │   │   ├── ref.d.ts
│       │   │   ├── ref.js
│       │   │   ├── syntax.d.ts
│       │   │   ├── syntax.js
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   ├── tsconfig.json
│       │   │   ├── type.d.ts
│       │   │   ├── type.js
│       │   │   ├── uri.d.ts
│       │   │   └── uri.js
│       │   ├── es-object-atoms
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── isObject.d.ts
│       │   │   ├── isObject.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── RequireObjectCoercible.d.ts
│       │   │   ├── RequireObjectCoercible.js
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   ├── ToObject.d.ts
│       │   │   ├── ToObject.js
│       │   │   └── tsconfig.json
│       │   ├── etag
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── express
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── application.js
│       │   │   │   ├── express.js
│       │   │   │   ├── request.js
│       │   │   │   ├── response.js
│       │   │   │   ├── utils.js
│       │   │   │   └── view.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── Readme.md
│       │   ├── fast-content-type-parse
│       │   │   ├── benchmarks
│       │   │   │   ├── simple.js
│       │   │   │   ├── simple-ows.js
│       │   │   │   ├── suite.js
│       │   │   │   ├── with-param.js
│       │   │   │   └── with-param-quoted.js
│       │   │   ├── eslint.config.js
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.test.js
│       │   │   └── types
│       │   │       ├── index.d.ts
│       │   │       └── index.test-d.ts
│       │   ├── fill-range
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── finalhandler
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── forwarded
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── fresh
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── function-bind
│       │   │   ├── CHANGELOG.md
│       │   │   ├── implementation.js
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── test
│       │   │       └── index.js
│       │   ├── get-intrinsic
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── test
│       │   │       └── GetIntrinsic.js
│       │   ├── get-proto
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── Object.getPrototypeOf.d.ts
│       │   │   ├── Object.getPrototypeOf.js
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── Reflect.getPrototypeOf.d.ts
│       │   │   ├── Reflect.getPrototypeOf.js
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── glob-parent
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── gopd
│       │   │   ├── CHANGELOG.md
│       │   │   ├── gOPD.d.ts
│       │   │   ├── gOPD.js
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── has-flag
│       │   │   ├── index.js
│       │   │   ├── license
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── hasown
│       │   │   ├── CHANGELOG.md
│       │   │   ├── eslint.config.mjs
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── tsconfig.json
│       │   ├── has-symbols
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── shams.d.ts
│       │   │   ├── shams.js
│       │   │   ├── test
│       │   │   │   ├── index.js
│       │   │   │   ├── shams
│       │   │   │   │   ├── core-js.js
│       │   │   │   │   └── get-own-property-symbols.js
│       │   │   │   └── tests.js
│       │   │   └── tsconfig.json
│       │   ├── http-errors
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── iconv-lite
│       │   │   ├── encodings
│       │   │   │   ├── dbcs-codec.js
│       │   │   │   ├── dbcs-data.js
│       │   │   │   ├── index.js
│       │   │   │   ├── internal.js
│       │   │   │   ├── sbcs-codec.js
│       │   │   │   ├── sbcs-data-generated.js
│       │   │   │   ├── sbcs-data.js
│       │   │   │   ├── tables
│       │   │   │   │   ├── big5-added.json
│       │   │   │   │   ├── cp936.json
│       │   │   │   │   ├── cp949.json
│       │   │   │   │   ├── cp950.json
│       │   │   │   │   ├── eucjp.json
│       │   │   │   │   ├── gb18030-ranges.json
│       │   │   │   │   ├── gbk-added.json
│       │   │   │   │   └── shiftjis.json
│       │   │   │   ├── utf16.js
│       │   │   │   ├── utf32.js
│       │   │   │   └── utf7.js
│       │   │   ├── lib
│       │   │   │   ├── bom-handling.js
│       │   │   │   ├── helpers
│       │   │   │   │   └── merge-exports.js
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── index.js
│       │   │   │   └── streams.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── types
│       │   │       └── encodings.d.ts
│       │   ├── ignore-by-default
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── inherits
│       │   │   ├── inherits_browser.js
│       │   │   ├── inherits.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── ipaddr.js
│       │   │   ├── ipaddr.min.js
│       │   │   ├── lib
│       │   │   │   ├── ipaddr.js
│       │   │   │   └── ipaddr.js.d.ts
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── is-binary-path
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── license
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── is-extglob
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── is-glob
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── is-number
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── is-promise
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── index.mjs
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── @jridgewell
│       │   │   ├── resolve-uri
│       │   │   │   ├── dist
│       │   │   │   │   ├── resolve-uri.mjs
│       │   │   │   │   ├── resolve-uri.mjs.map
│       │   │   │   │   ├── resolve-uri.umd.js
│       │   │   │   │   ├── resolve-uri.umd.js.map
│       │   │   │   │   └── types
│       │   │   │   │       └── resolve-uri.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── sourcemap-codec
│       │   │   │   ├── dist
│       │   │   │   │   ├── sourcemap-codec.mjs
│       │   │   │   │   ├── sourcemap-codec.mjs.map
│       │   │   │   │   ├── sourcemap-codec.umd.js
│       │   │   │   │   └── sourcemap-codec.umd.js.map
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   ├── README.md
│       │   │   │   ├── src
│       │   │   │   │   ├── scopes.ts
│       │   │   │   │   ├── sourcemap-codec.ts
│       │   │   │   │   ├── strings.ts
│       │   │   │   │   └── vlq.ts
│       │   │   │   └── types
│       │   │   │       ├── scopes.d.cts
│       │   │   │       ├── scopes.d.cts.map
│       │   │   │       ├── scopes.d.mts
│       │   │   │       ├── scopes.d.mts.map
│       │   │   │       ├── sourcemap-codec.d.cts
│       │   │   │       ├── sourcemap-codec.d.cts.map
│       │   │   │       ├── sourcemap-codec.d.mts
│       │   │   │       ├── sourcemap-codec.d.mts.map
│       │   │   │       ├── strings.d.cts
│       │   │   │       ├── strings.d.cts.map
│       │   │   │       ├── strings.d.mts
│       │   │   │       ├── strings.d.mts.map
│       │   │   │       ├── vlq.d.cts
│       │   │   │       ├── vlq.d.cts.map
│       │   │   │       ├── vlq.d.mts
│       │   │   │       └── vlq.d.mts.map
│       │   │   └── trace-mapping
│       │   │       ├── dist
│       │   │       │   ├── trace-mapping.mjs
│       │   │       │   ├── trace-mapping.mjs.map
│       │   │       │   ├── trace-mapping.umd.js
│       │   │       │   ├── trace-mapping.umd.js.map
│       │   │       │   └── types
│       │   │       │       ├── any-map.d.ts
│       │   │       │       ├── binary-search.d.ts
│       │   │       │       ├── by-source.d.ts
│       │   │       │       ├── resolve.d.ts
│       │   │       │       ├── sort.d.ts
│       │   │       │       ├── sourcemap-segment.d.ts
│       │   │       │       ├── strip-filename.d.ts
│       │   │       │       ├── trace-mapping.d.ts
│       │   │       │       └── types.d.ts
│       │   │       ├── LICENSE
│       │   │       ├── package.json
│       │   │       └── README.md
│       │   ├── json-with-bigint
│       │   │   ├── json-with-bigint.cjs
│       │   │   ├── json-with-bigint.d.cts
│       │   │   ├── json-with-bigint.d.ts
│       │   │   ├── json-with-bigint.js
│       │   │   ├── json-with-bigint.min.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── scripts
│       │   │   │   └── build-cjs.js
│       │   │   ├── __tests__
│       │   │   │   ├── helpers.cjs
│       │   │   │   ├── performance.cjs
│       │   │   │   ├── performance.mjs
│       │   │   │   ├── unit.cjs
│       │   │   │   └── unit.mjs
│       │   │   └── tsconfig.json
│       │   ├── make-error
│       │   │   ├── dist
│       │   │   │   └── make-error.js
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── math-intrinsics
│       │   │   ├── abs.d.ts
│       │   │   ├── abs.js
│       │   │   ├── CHANGELOG.md
│       │   │   ├── constants
│       │   │   │   ├── maxArrayLength.d.ts
│       │   │   │   ├── maxArrayLength.js
│       │   │   │   ├── maxSafeInteger.d.ts
│       │   │   │   ├── maxSafeInteger.js
│       │   │   │   ├── maxValue.d.ts
│       │   │   │   └── maxValue.js
│       │   │   ├── floor.d.ts
│       │   │   ├── floor.js
│       │   │   ├── isFinite.d.ts
│       │   │   ├── isFinite.js
│       │   │   ├── isInteger.d.ts
│       │   │   ├── isInteger.js
│       │   │   ├── isNaN.d.ts
│       │   │   ├── isNaN.js
│       │   │   ├── isNegativeZero.d.ts
│       │   │   ├── isNegativeZero.js
│       │   │   ├── LICENSE
│       │   │   ├── max.d.ts
│       │   │   ├── max.js
│       │   │   ├── min.d.ts
│       │   │   ├── min.js
│       │   │   ├── mod.d.ts
│       │   │   ├── mod.js
│       │   │   ├── package.json
│       │   │   ├── pow.d.ts
│       │   │   ├── pow.js
│       │   │   ├── README.md
│       │   │   ├── round.d.ts
│       │   │   ├── round.js
│       │   │   ├── sign.d.ts
│       │   │   ├── sign.js
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── media-typer
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── merge-descriptors
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── license
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── mime-db
│       │   │   ├── db.json
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── mime-types
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── mimeScore.js
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── minimatch
│       │   │   ├── dist
│       │   │   │   ├── commonjs
│       │   │   │   │   ├── assert-valid-pattern.d.ts
│       │   │   │   │   ├── assert-valid-pattern.d.ts.map
│       │   │   │   │   ├── assert-valid-pattern.js
│       │   │   │   │   ├── assert-valid-pattern.js.map
│       │   │   │   │   ├── ast.d.ts
│       │   │   │   │   ├── ast.d.ts.map
│       │   │   │   │   ├── ast.js
│       │   │   │   │   ├── ast.js.map
│       │   │   │   │   ├── brace-expressions.d.ts
│       │   │   │   │   ├── brace-expressions.d.ts.map
│       │   │   │   │   ├── brace-expressions.js
│       │   │   │   │   ├── brace-expressions.js.map
│       │   │   │   │   ├── escape.d.ts
│       │   │   │   │   ├── escape.d.ts.map
│       │   │   │   │   ├── escape.js
│       │   │   │   │   ├── escape.js.map
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── index.d.ts.map
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── index.js.map
│       │   │   │   │   ├── package.json
│       │   │   │   │   ├── unescape.d.ts
│       │   │   │   │   ├── unescape.d.ts.map
│       │   │   │   │   ├── unescape.js
│       │   │   │   │   └── unescape.js.map
│       │   │   │   └── esm
│       │   │   │       ├── assert-valid-pattern.d.ts
│       │   │   │       ├── assert-valid-pattern.d.ts.map
│       │   │   │       ├── assert-valid-pattern.js
│       │   │   │       ├── assert-valid-pattern.js.map
│       │   │   │       ├── ast.d.ts
│       │   │   │       ├── ast.d.ts.map
│       │   │   │       ├── ast.js
│       │   │   │       ├── ast.js.map
│       │   │   │       ├── brace-expressions.d.ts
│       │   │   │       ├── brace-expressions.d.ts.map
│       │   │   │       ├── brace-expressions.js
│       │   │   │       ├── brace-expressions.js.map
│       │   │   │       ├── escape.d.ts
│       │   │   │       ├── escape.d.ts.map
│       │   │   │       ├── escape.js
│       │   │   │       ├── escape.js.map
│       │   │   │       ├── index.d.ts
│       │   │   │       ├── index.d.ts.map
│       │   │   │       ├── index.js
│       │   │   │       ├── index.js.map
│       │   │   │       ├── package.json
│       │   │   │       ├── unescape.d.ts
│       │   │   │       ├── unescape.d.ts.map
│       │   │   │       ├── unescape.js
│       │   │   │       └── unescape.js.map
│       │   │   ├── LICENSE.md
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── ms
│       │   │   ├── index.js
│       │   │   ├── license.md
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── negotiator
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── charset.js
│       │   │   │   ├── encoding.js
│       │   │   │   ├── language.js
│       │   │   │   └── mediaType.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── nodemon
│       │   │   ├── bin
│       │   │   │   ├── nodemon.js
│       │   │   │   └── windows-kill.exe
│       │   │   ├── doc
│       │   │   │   └── cli
│       │   │   │       ├── authors.txt
│       │   │   │       ├── config.txt
│       │   │   │       ├── help.txt
│       │   │   │       ├── logo.txt
│       │   │   │       ├── options.txt
│       │   │   │       ├── topics.txt
│       │   │   │       ├── usage.txt
│       │   │   │       └── whoami.txt
│       │   │   ├── index.d.ts
│       │   │   ├── jsconfig.json
│       │   │   ├── lib
│       │   │   │   ├── cli
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── parse.js
│       │   │   │   ├── config
│       │   │   │   │   ├── command.js
│       │   │   │   │   ├── defaults.js
│       │   │   │   │   ├── exec.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── load.js
│       │   │   │   ├── help
│       │   │   │   │   └── index.js
│       │   │   │   ├── index.js
│       │   │   │   ├── monitor
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── match.js
│       │   │   │   │   ├── run.js
│       │   │   │   │   ├── signals.js
│       │   │   │   │   └── watch.js
│       │   │   │   ├── nodemon.js
│       │   │   │   ├── rules
│       │   │   │   │   ├── add.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── parse.js
│       │   │   │   ├── spawn.js
│       │   │   │   ├── utils
│       │   │   │   │   ├── bus.js
│       │   │   │   │   ├── clone.js
│       │   │   │   │   ├── colour.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── log.js
│       │   │   │   │   └── merge.js
│       │   │   │   └── version.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── normalize-path
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── object-inspect
│       │   │   ├── CHANGELOG.md
│       │   │   ├── example
│       │   │   │   ├── all.js
│       │   │   │   ├── circular.js
│       │   │   │   ├── fn.js
│       │   │   │   └── inspect.js
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── package-support.json
│       │   │   ├── readme.markdown
│       │   │   ├── test
│       │   │   │   ├── bigint.js
│       │   │   │   ├── browser
│       │   │   │   │   └── dom.js
│       │   │   │   ├── circular.js
│       │   │   │   ├── deep.js
│       │   │   │   ├── element.js
│       │   │   │   ├── err.js
│       │   │   │   ├── fakes.js
│       │   │   │   ├── fn.js
│       │   │   │   ├── global.js
│       │   │   │   ├── has.js
│       │   │   │   ├── holes.js
│       │   │   │   ├── indent-option.js
│       │   │   │   ├── inspect.js
│       │   │   │   ├── lowbyte.js
│       │   │   │   ├── number.js
│       │   │   │   ├── quoteStyle.js
│       │   │   │   ├── toStringTag.js
│       │   │   │   ├── undef.js
│       │   │   │   └── values.js
│       │   │   ├── test-core-js.js
│       │   │   └── util.inspect.js
│       │   ├── @octokit
│       │   │   ├── auth-token
│       │   │   │   ├── dist-bundle
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── index.js.map
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── auth.js
│       │   │   │   │   ├── hook.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── is-jwt.js
│       │   │   │   │   └── with-authorization-prefix.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── auth.d.ts
│       │   │   │   │   ├── hook.d.ts
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── is-jwt.d.ts
│       │   │   │   │   ├── types.d.ts
│       │   │   │   │   └── with-authorization-prefix.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── core
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── version.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── types.d.ts
│       │   │   │   │   └── version.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── endpoint
│       │   │   │   ├── dist-bundle
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── index.js.map
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── defaults.js
│       │   │   │   │   ├── endpoint-with-defaults.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── merge.js
│       │   │   │   │   ├── parse.js
│       │   │   │   │   ├── util
│       │   │   │   │   │   ├── add-query-parameters.js
│       │   │   │   │   │   ├── extract-url-variable-names.js
│       │   │   │   │   │   ├── is-plain-object.js
│       │   │   │   │   │   ├── lowercase-keys.js
│       │   │   │   │   │   ├── merge-deep.js
│       │   │   │   │   │   ├── omit.js
│       │   │   │   │   │   ├── remove-undefined-properties.js
│       │   │   │   │   │   └── url-template.js
│       │   │   │   │   ├── version.js
│       │   │   │   │   └── with-defaults.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── defaults.d.ts
│       │   │   │   │   ├── endpoint-with-defaults.d.ts
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── merge.d.ts
│       │   │   │   │   ├── parse.d.ts
│       │   │   │   │   ├── util
│       │   │   │   │   │   ├── add-query-parameters.d.ts
│       │   │   │   │   │   ├── extract-url-variable-names.d.ts
│       │   │   │   │   │   ├── is-plain-object.d.ts
│       │   │   │   │   │   ├── lowercase-keys.d.ts
│       │   │   │   │   │   ├── merge-deep.d.ts
│       │   │   │   │   │   ├── omit.d.ts
│       │   │   │   │   │   ├── remove-undefined-properties.d.ts
│       │   │   │   │   │   └── url-template.d.ts
│       │   │   │   │   ├── version.d.ts
│       │   │   │   │   └── with-defaults.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── graphql
│       │   │   │   ├── dist-bundle
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── index.js.map
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── error.js
│       │   │   │   │   ├── graphql.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── version.js
│       │   │   │   │   └── with-defaults.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── error.d.ts
│       │   │   │   │   ├── graphql.d.ts
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── types.d.ts
│       │   │   │   │   ├── version.d.ts
│       │   │   │   │   └── with-defaults.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── openapi-types
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   ├── README.md
│       │   │   │   └── types.d.ts
│       │   │   ├── plugin-paginate-rest
│       │   │   │   ├── dist-bundle
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── index.js.map
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── compose-paginate.js
│       │   │   │   │   ├── generated
│       │   │   │   │   │   └── paginating-endpoints.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── iterator.js
│       │   │   │   │   ├── normalize-paginated-list-response.js
│       │   │   │   │   ├── paginate.js
│       │   │   │   │   ├── paginating-endpoints.js
│       │   │   │   │   └── version.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── compose-paginate.d.ts
│       │   │   │   │   ├── generated
│       │   │   │   │   │   └── paginating-endpoints.d.ts
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── iterator.d.ts
│       │   │   │   │   ├── normalize-paginated-list-response.d.ts
│       │   │   │   │   ├── paginate.d.ts
│       │   │   │   │   ├── paginating-endpoints.d.ts
│       │   │   │   │   ├── types.d.ts
│       │   │   │   │   └── version.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── plugin-request-log
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── version.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   └── version.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── plugin-rest-endpoint-methods
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── endpoints-to-methods.js
│       │   │   │   │   ├── endpoints-to-methods.js.map
│       │   │   │   │   ├── generated
│       │   │   │   │   │   ├── endpoints.js
│       │   │   │   │   │   └── endpoints.js.map
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── index.js.map
│       │   │   │   │   ├── version.js
│       │   │   │   │   └── version.js.map
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── endpoints-to-methods.d.ts
│       │   │   │   │   ├── generated
│       │   │   │   │   │   ├── endpoints.d.ts
│       │   │   │   │   │   ├── method-types.d.ts
│       │   │   │   │   │   └── parameters-and-response-types.d.ts
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── types.d.ts
│       │   │   │   │   └── version.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── request
│       │   │   │   ├── dist-bundle
│       │   │   │   │   ├── index.js
│       │   │   │   │   └── index.js.map
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── defaults.js
│       │   │   │   │   ├── fetch-wrapper.js
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── is-plain-object.js
│       │   │   │   │   ├── version.js
│       │   │   │   │   └── with-defaults.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── defaults.d.ts
│       │   │   │   │   ├── fetch-wrapper.d.ts
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   ├── is-plain-object.d.ts
│       │   │   │   │   ├── version.d.ts
│       │   │   │   │   └── with-defaults.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── node_modules
│       │   │   │   │   └── content-type
│       │   │   │   │       ├── dist
│       │   │   │   │       │   ├── index.d.ts
│       │   │   │   │       │   ├── index.js
│       │   │   │   │       │   └── index.js.map
│       │   │   │   │       ├── LICENSE
│       │   │   │   │       ├── package.json
│       │   │   │   │       └── README.md
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── request-error
│       │   │   │   ├── dist-src
│       │   │   │   │   └── index.js
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   └── types.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── rest
│       │   │   │   ├── dist-src
│       │   │   │   │   ├── index.js
│       │   │   │   │   ├── index.js.map
│       │   │   │   │   ├── version.js
│       │   │   │   │   └── version.js.map
│       │   │   │   ├── dist-types
│       │   │   │   │   ├── index.d.ts
│       │   │   │   │   └── version.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   └── types
│       │   │       ├── dist-types
│       │   │       │   ├── AuthInterface.d.ts
│       │   │       │   ├── EndpointDefaults.d.ts
│       │   │       │   ├── EndpointInterface.d.ts
│       │   │       │   ├── EndpointOptions.d.ts
│       │   │       │   ├── Fetch.d.ts
│       │   │       │   ├── generated
│       │   │       │   │   └── Endpoints.d.ts
│       │   │       │   ├── GetResponseTypeFromEndpointMethod.d.ts
│       │   │       │   ├── index.d.ts
│       │   │       │   ├── OctokitResponse.d.ts
│       │   │       │   ├── RequestError.d.ts
│       │   │       │   ├── RequestHeaders.d.ts
│       │   │       │   ├── RequestInterface.d.ts
│       │   │       │   ├── RequestMethod.d.ts
│       │   │       │   ├── RequestOptions.d.ts
│       │   │       │   ├── RequestParameters.d.ts
│       │   │       │   ├── RequestRequestOptions.d.ts
│       │   │       │   ├── ResponseHeaders.d.ts
│       │   │       │   ├── Route.d.ts
│       │   │       │   ├── StrategyInterface.d.ts
│       │   │       │   ├── Url.d.ts
│       │   │       │   └── VERSION.d.ts
│       │   │       ├── LICENSE
│       │   │       ├── package.json
│       │   │       └── README.md
│       │   ├── once
│       │   │   ├── LICENSE
│       │   │   ├── once.js
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── on-finished
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── parseurl
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── path-to-regexp
│       │   │   ├── dist
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── index.js
│       │   │   │   └── index.js.map
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── Readme.md
│       │   ├── picomatch
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── constants.js
│       │   │   │   ├── parse.js
│       │   │   │   ├── picomatch.js
│       │   │   │   ├── scan.js
│       │   │   │   └── utils.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── proxy-addr
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── pstree.remy
│       │   │   ├── lib
│       │   │   │   ├── index.js
│       │   │   │   ├── tree.js
│       │   │   │   └── utils.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── tests
│       │   │       ├── fixtures
│       │   │       │   ├── index.js
│       │   │       │   ├── out1
│       │   │       │   └── out2
│       │   │       └── index.test.js
│       │   ├── qs
│       │   │   ├── CHANGELOG.md
│       │   │   ├── dist
│       │   │   │   └── qs.js
│       │   │   ├── eslint.config.mjs
│       │   │   ├── lib
│       │   │   │   ├── formats.js
│       │   │   │   ├── index.js
│       │   │   │   ├── parse.js
│       │   │   │   ├── stringify.js
│       │   │   │   └── utils.js
│       │   │   ├── LICENSE.md
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── test
│       │   │       ├── empty-keys-cases.js
│       │   │       ├── parse.js
│       │   │       ├── stringify.js
│       │   │       └── utils.js
│       │   ├── range-parser
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── raw-body
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── readdirp
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── router
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── lib
│       │   │   │   ├── layer.js
│       │   │   │   └── route.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── safer-buffer
│       │   │   ├── dangerous.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── Porting-Buffer.md
│       │   │   ├── Readme.md
│       │   │   ├── safer.js
│       │   │   └── tests.js
│       │   ├── semver
│       │   │   ├── bin
│       │   │   │   └── semver.js
│       │   │   ├── classes
│       │   │   │   ├── comparator.js
│       │   │   │   ├── index.js
│       │   │   │   ├── range.js
│       │   │   │   └── semver.js
│       │   │   ├── functions
│       │   │   │   ├── clean.js
│       │   │   │   ├── cmp.js
│       │   │   │   ├── coerce.js
│       │   │   │   ├── compare-build.js
│       │   │   │   ├── compare.js
│       │   │   │   ├── compare-loose.js
│       │   │   │   ├── diff.js
│       │   │   │   ├── eq.js
│       │   │   │   ├── gte.js
│       │   │   │   ├── gt.js
│       │   │   │   ├── inc.js
│       │   │   │   ├── lte.js
│       │   │   │   ├── lt.js
│       │   │   │   ├── major.js
│       │   │   │   ├── minor.js
│       │   │   │   ├── neq.js
│       │   │   │   ├── parse.js
│       │   │   │   ├── patch.js
│       │   │   │   ├── prerelease.js
│       │   │   │   ├── rcompare.js
│       │   │   │   ├── rsort.js
│       │   │   │   ├── satisfies.js
│       │   │   │   ├── sort.js
│       │   │   │   ├── truncate.js
│       │   │   │   └── valid.js
│       │   │   ├── index.js
│       │   │   ├── internal
│       │   │   │   ├── constants.js
│       │   │   │   ├── debug.js
│       │   │   │   ├── identifiers.js
│       │   │   │   ├── lrucache.js
│       │   │   │   ├── parse-options.js
│       │   │   │   └── re.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── preload.js
│       │   │   ├── range.bnf
│       │   │   ├── ranges
│       │   │   │   ├── gtr.js
│       │   │   │   ├── intersects.js
│       │   │   │   ├── ltr.js
│       │   │   │   ├── max-satisfying.js
│       │   │   │   ├── min-satisfying.js
│       │   │   │   ├── min-version.js
│       │   │   │   ├── outside.js
│       │   │   │   ├── simplify.js
│       │   │   │   ├── subset.js
│       │   │   │   ├── to-comparators.js
│       │   │   │   └── valid.js
│       │   │   └── README.md
│       │   ├── send
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── serve-static
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── setprototypeof
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── test
│       │   │       └── index.js
│       │   ├── side-channel
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── side-channel-list
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── list.d.ts
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── side-channel-map
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── side-channel-weakmap
│       │   │   ├── CHANGELOG.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── test
│       │   │   │   └── index.js
│       │   │   └── tsconfig.json
│       │   ├── simple-update-notifier
│       │   │   ├── build
│       │   │   │   ├── index.d.ts
│       │   │   │   └── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── src
│       │   │       ├── borderedText.ts
│       │   │       ├── cache.spec.ts
│       │   │       ├── cache.ts
│       │   │       ├── getDistVersion.spec.ts
│       │   │       ├── getDistVersion.ts
│       │   │       ├── hasNewVersion.spec.ts
│       │   │       ├── hasNewVersion.ts
│       │   │       ├── index.spec.ts
│       │   │       ├── index.ts
│       │   │       ├── isNpmOrYarn.ts
│       │   │       └── types.ts
│       │   ├── statuses
│       │   │   ├── codes.json
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── supports-color
│       │   │   ├── browser.js
│       │   │   ├── index.js
│       │   │   ├── license
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   ├── toidentifier
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── to-regex-range
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── touch
│       │   │   ├── bin
│       │   │   │   └── nodetouch.js
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── @tsconfig
│       │   │   ├── node10
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   ├── README.md
│       │   │   │   └── tsconfig.json
│       │   │   ├── node12
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   ├── README.md
│       │   │   │   └── tsconfig.json
│       │   │   ├── node14
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   ├── README.md
│       │   │   │   └── tsconfig.json
│       │   │   └── node16
│       │   │       ├── LICENSE
│       │   │       ├── package.json
│       │   │       ├── README.md
│       │   │       └── tsconfig.json
│       │   ├── ts-node
│       │   │   ├── child-loader.mjs
│       │   │   ├── dist
│       │   │   │   ├── bin-cwd.d.ts
│       │   │   │   ├── bin-cwd.js
│       │   │   │   ├── bin-cwd.js.map
│       │   │   │   ├── bin.d.ts
│       │   │   │   ├── bin-esm.d.ts
│       │   │   │   ├── bin-esm.js
│       │   │   │   ├── bin-esm.js.map
│       │   │   │   ├── bin.js
│       │   │   │   ├── bin.js.map
│       │   │   │   ├── bin-script-deprecated.d.ts
│       │   │   │   ├── bin-script-deprecated.js
│       │   │   │   ├── bin-script-deprecated.js.map
│       │   │   │   ├── bin-script.d.ts
│       │   │   │   ├── bin-script.js
│       │   │   │   ├── bin-script.js.map
│       │   │   │   ├── bin-transpile.d.ts
│       │   │   │   ├── bin-transpile.js
│       │   │   │   ├── bin-transpile.js.map
│       │   │   │   ├── child
│       │   │   │   │   ├── argv-payload.d.ts
│       │   │   │   │   ├── argv-payload.js
│       │   │   │   │   ├── argv-payload.js.map
│       │   │   │   │   ├── child-entrypoint.d.ts
│       │   │   │   │   ├── child-entrypoint.js
│       │   │   │   │   ├── child-entrypoint.js.map
│       │   │   │   │   ├── child-loader.d.ts
│       │   │   │   │   ├── child-loader.js
│       │   │   │   │   ├── child-loader.js.map
│       │   │   │   │   ├── child-require.d.ts
│       │   │   │   │   ├── child-require.js
│       │   │   │   │   ├── child-require.js.map
│       │   │   │   │   ├── spawn-child.d.ts
│       │   │   │   │   ├── spawn-child.js
│       │   │   │   │   └── spawn-child.js.map
│       │   │   │   ├── cjs-resolve-hooks.d.ts
│       │   │   │   ├── cjs-resolve-hooks.js
│       │   │   │   ├── cjs-resolve-hooks.js.map
│       │   │   │   ├── configuration.d.ts
│       │   │   │   ├── configuration.js
│       │   │   │   ├── configuration.js.map
│       │   │   │   ├── esm.d.ts
│       │   │   │   ├── esm.js
│       │   │   │   ├── esm.js.map
│       │   │   │   ├── file-extensions.d.ts
│       │   │   │   ├── file-extensions.js
│       │   │   │   ├── file-extensions.js.map
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── index.js
│       │   │   │   ├── index.js.map
│       │   │   │   ├── module-type-classifier.d.ts
│       │   │   │   ├── module-type-classifier.js
│       │   │   │   ├── module-type-classifier.js.map
│       │   │   │   ├── node-module-type-classifier.d.ts
│       │   │   │   ├── node-module-type-classifier.js
│       │   │   │   ├── node-module-type-classifier.js.map
│       │   │   │   ├── repl.d.ts
│       │   │   │   ├── repl.js
│       │   │   │   ├── repl.js.map
│       │   │   │   ├── resolver-functions.d.ts
│       │   │   │   ├── resolver-functions.js
│       │   │   │   ├── resolver-functions.js.map
│       │   │   │   ├── transpilers
│       │   │   │   │   ├── swc.d.ts
│       │   │   │   │   ├── swc.js
│       │   │   │   │   ├── swc.js.map
│       │   │   │   │   ├── types.d.ts
│       │   │   │   │   ├── types.js
│       │   │   │   │   └── types.js.map
│       │   │   │   ├── ts-compiler-types.d.ts
│       │   │   │   ├── ts-compiler-types.js
│       │   │   │   ├── ts-compiler-types.js.map
│       │   │   │   ├── tsconfig-schema.d.ts
│       │   │   │   ├── tsconfig-schema.js
│       │   │   │   ├── tsconfig-schema.js.map
│       │   │   │   ├── tsconfigs.d.ts
│       │   │   │   ├── tsconfigs.js
│       │   │   │   ├── tsconfigs.js.map
│       │   │   │   ├── ts-internals.d.ts
│       │   │   │   ├── ts-internals.js
│       │   │   │   ├── ts-internals.js.map
│       │   │   │   ├── ts-transpile-module.d.ts
│       │   │   │   ├── ts-transpile-module.js
│       │   │   │   ├── ts-transpile-module.js.map
│       │   │   │   ├── util.d.ts
│       │   │   │   ├── util.js
│       │   │   │   └── util.js.map
│       │   │   ├── dist-raw
│       │   │   │   ├── node-internalBinding-fs.js
│       │   │   │   ├── node-internal-constants.js
│       │   │   │   ├── node-internal-errors.js
│       │   │   │   ├── node-internal-modules-cjs-helpers.js
│       │   │   │   ├── node-internal-modules-cjs-loader.js
│       │   │   │   ├── node-internal-modules-esm-get_format.js
│       │   │   │   ├── node-internal-modules-esm-resolve.js
│       │   │   │   ├── node-internal-modules-package_json_reader.js
│       │   │   │   ├── node-internal-repl-await.js
│       │   │   │   ├── NODE-LICENSE.md
│       │   │   │   ├── node-nativemodule.js
│       │   │   │   ├── node-options.js
│       │   │   │   ├── node-primordials.js
│       │   │   │   ├── README.md
│       │   │   │   └── runmain-hack.js
│       │   │   ├── esm
│       │   │   │   └── transpile-only.mjs
│       │   │   ├── esm.mjs
│       │   │   ├── LICENSE
│       │   │   ├── node10
│       │   │   │   └── tsconfig.json
│       │   │   ├── node12
│       │   │   │   └── tsconfig.json
│       │   │   ├── node14
│       │   │   │   └── tsconfig.json
│       │   │   ├── node16
│       │   │   │   └── tsconfig.json
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── register
│       │   │   │   ├── files.js
│       │   │   │   ├── index.js
│       │   │   │   ├── transpile-only.js
│       │   │   │   └── type-check.js
│       │   │   ├── transpilers
│       │   │   │   ├── swc-experimental.js
│       │   │   │   └── swc.js
│       │   │   ├── tsconfig.schema.json
│       │   │   └── tsconfig.schemastore-schema.json
│       │   ├── type-is
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── node_modules
│       │   │   │   └── content-type
│       │   │   │       ├── dist
│       │   │   │       │   ├── index.d.ts
│       │   │   │       │   ├── index.js
│       │   │   │       │   └── index.js.map
│       │   │   │       ├── LICENSE
│       │   │   │       ├── package.json
│       │   │   │       └── README.md
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── @types
│       │   │   ├── body-parser
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── bun
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── connect
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── express
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── express-serve-static-core
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── http-errors
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── node
│       │   │   │   ├── assert
│       │   │   │   │   └── strict.d.ts
│       │   │   │   ├── assert.d.ts
│       │   │   │   ├── async_hooks.d.ts
│       │   │   │   ├── buffer.buffer.d.ts
│       │   │   │   ├── buffer.d.ts
│       │   │   │   ├── child_process.d.ts
│       │   │   │   ├── cluster.d.ts
│       │   │   │   ├── compatibility
│       │   │   │   │   └── iterators.d.ts
│       │   │   │   ├── console.d.ts
│       │   │   │   ├── constants.d.ts
│       │   │   │   ├── crypto.d.ts
│       │   │   │   ├── dgram.d.ts
│       │   │   │   ├── diagnostics_channel.d.ts
│       │   │   │   ├── dns
│       │   │   │   │   └── promises.d.ts
│       │   │   │   ├── dns.d.ts
│       │   │   │   ├── domain.d.ts
│       │   │   │   ├── events.d.ts
│       │   │   │   ├── fs
│       │   │   │   │   └── promises.d.ts
│       │   │   │   ├── fs.d.ts
│       │   │   │   ├── globals.d.ts
│       │   │   │   ├── globals.typedarray.d.ts
│       │   │   │   ├── http2.d.ts
│       │   │   │   ├── http.d.ts
│       │   │   │   ├── https.d.ts
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── inspector
│       │   │   │   │   └── promises.d.ts
│       │   │   │   ├── inspector.d.ts
│       │   │   │   ├── inspector.generated.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── module.d.ts
│       │   │   │   ├── net.d.ts
│       │   │   │   ├── os.d.ts
│       │   │   │   ├── package.json
│       │   │   │   ├── path
│       │   │   │   │   ├── posix.d.ts
│       │   │   │   │   └── win32.d.ts
│       │   │   │   ├── path.d.ts
│       │   │   │   ├── perf_hooks.d.ts
│       │   │   │   ├── process.d.ts
│       │   │   │   ├── punycode.d.ts
│       │   │   │   ├── querystring.d.ts
│       │   │   │   ├── quic.d.ts
│       │   │   │   ├── readline
│       │   │   │   │   └── promises.d.ts
│       │   │   │   ├── readline.d.ts
│       │   │   │   ├── README.md
│       │   │   │   ├── repl.d.ts
│       │   │   │   ├── sea.d.ts
│       │   │   │   ├── sqlite.d.ts
│       │   │   │   ├── stream
│       │   │   │   │   ├── consumers.d.ts
│       │   │   │   │   ├── promises.d.ts
│       │   │   │   │   └── web.d.ts
│       │   │   │   ├── stream.d.ts
│       │   │   │   ├── string_decoder.d.ts
│       │   │   │   ├── test
│       │   │   │   │   └── reporters.d.ts
│       │   │   │   ├── test.d.ts
│       │   │   │   ├── timers
│       │   │   │   │   └── promises.d.ts
│       │   │   │   ├── timers.d.ts
│       │   │   │   ├── tls.d.ts
│       │   │   │   ├── trace_events.d.ts
│       │   │   │   ├── ts5.6
│       │   │   │   │   ├── buffer.buffer.d.ts
│       │   │   │   │   ├── compatibility
│       │   │   │   │   │   └── float16array.d.ts
│       │   │   │   │   ├── globals.typedarray.d.ts
│       │   │   │   │   └── index.d.ts
│       │   │   │   ├── ts5.7
│       │   │   │   │   ├── compatibility
│       │   │   │   │   │   └── float16array.d.ts
│       │   │   │   │   └── index.d.ts
│       │   │   │   ├── tty.d.ts
│       │   │   │   ├── url.d.ts
│       │   │   │   ├── util
│       │   │   │   │   └── types.d.ts
│       │   │   │   ├── util.d.ts
│       │   │   │   ├── v8.d.ts
│       │   │   │   ├── vm.d.ts
│       │   │   │   ├── wasi.d.ts
│       │   │   │   ├── web-globals
│       │   │   │   │   ├── abortcontroller.d.ts
│       │   │   │   │   ├── blob.d.ts
│       │   │   │   │   ├── console.d.ts
│       │   │   │   │   ├── crypto.d.ts
│       │   │   │   │   ├── domexception.d.ts
│       │   │   │   │   ├── encoding.d.ts
│       │   │   │   │   ├── events.d.ts
│       │   │   │   │   ├── fetch.d.ts
│       │   │   │   │   ├── importmeta.d.ts
│       │   │   │   │   ├── messaging.d.ts
│       │   │   │   │   ├── navigator.d.ts
│       │   │   │   │   ├── performance.d.ts
│       │   │   │   │   ├── storage.d.ts
│       │   │   │   │   ├── streams.d.ts
│       │   │   │   │   ├── timers.d.ts
│       │   │   │   │   └── url.d.ts
│       │   │   │   ├── worker_threads.d.ts
│       │   │   │   └── zlib.d.ts
│       │   │   ├── qs
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── range-parser
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   ├── send
│       │   │   │   ├── index.d.ts
│       │   │   │   ├── LICENSE
│       │   │   │   ├── package.json
│       │   │   │   └── README.md
│       │   │   └── serve-static
│       │   │       ├── index.d.ts
│       │   │       ├── LICENSE
│       │   │       ├── package.json
│       │   │       └── README.md
│       │   ├── typescript
│       │   │   ├── bin
│       │   │   │   ├── tsc
│       │   │   │   └── tsserver
│       │   │   ├── lib
│       │   │   │   ├── cs
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── de
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── es
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── fr
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── it
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── ja
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── ko
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── lib.decorators.d.ts
│       │   │   │   ├── lib.decorators.legacy.d.ts
│       │   │   │   ├── lib.dom.asynciterable.d.ts
│       │   │   │   ├── lib.dom.d.ts
│       │   │   │   ├── lib.dom.iterable.d.ts
│       │   │   │   ├── lib.d.ts
│       │   │   │   ├── lib.es2015.collection.d.ts
│       │   │   │   ├── lib.es2015.core.d.ts
│       │   │   │   ├── lib.es2015.d.ts
│       │   │   │   ├── lib.es2015.generator.d.ts
│       │   │   │   ├── lib.es2015.iterable.d.ts
│       │   │   │   ├── lib.es2015.promise.d.ts
│       │   │   │   ├── lib.es2015.proxy.d.ts
│       │   │   │   ├── lib.es2015.reflect.d.ts
│       │   │   │   ├── lib.es2015.symbol.d.ts
│       │   │   │   ├── lib.es2015.symbol.wellknown.d.ts
│       │   │   │   ├── lib.es2016.array.include.d.ts
│       │   │   │   ├── lib.es2016.d.ts
│       │   │   │   ├── lib.es2016.full.d.ts
│       │   │   │   ├── lib.es2016.intl.d.ts
│       │   │   │   ├── lib.es2017.arraybuffer.d.ts
│       │   │   │   ├── lib.es2017.date.d.ts
│       │   │   │   ├── lib.es2017.d.ts
│       │   │   │   ├── lib.es2017.full.d.ts
│       │   │   │   ├── lib.es2017.intl.d.ts
│       │   │   │   ├── lib.es2017.object.d.ts
│       │   │   │   ├── lib.es2017.sharedmemory.d.ts
│       │   │   │   ├── lib.es2017.string.d.ts
│       │   │   │   ├── lib.es2017.typedarrays.d.ts
│       │   │   │   ├── lib.es2018.asyncgenerator.d.ts
│       │   │   │   ├── lib.es2018.asynciterable.d.ts
│       │   │   │   ├── lib.es2018.d.ts
│       │   │   │   ├── lib.es2018.full.d.ts
│       │   │   │   ├── lib.es2018.intl.d.ts
│       │   │   │   ├── lib.es2018.promise.d.ts
│       │   │   │   ├── lib.es2018.regexp.d.ts
│       │   │   │   ├── lib.es2019.array.d.ts
│       │   │   │   ├── lib.es2019.d.ts
│       │   │   │   ├── lib.es2019.full.d.ts
│       │   │   │   ├── lib.es2019.intl.d.ts
│       │   │   │   ├── lib.es2019.object.d.ts
│       │   │   │   ├── lib.es2019.string.d.ts
│       │   │   │   ├── lib.es2019.symbol.d.ts
│       │   │   │   ├── lib.es2020.bigint.d.ts
│       │   │   │   ├── lib.es2020.date.d.ts
│       │   │   │   ├── lib.es2020.d.ts
│       │   │   │   ├── lib.es2020.full.d.ts
│       │   │   │   ├── lib.es2020.intl.d.ts
│       │   │   │   ├── lib.es2020.number.d.ts
│       │   │   │   ├── lib.es2020.promise.d.ts
│       │   │   │   ├── lib.es2020.sharedmemory.d.ts
│       │   │   │   ├── lib.es2020.string.d.ts
│       │   │   │   ├── lib.es2020.symbol.wellknown.d.ts
│       │   │   │   ├── lib.es2021.d.ts
│       │   │   │   ├── lib.es2021.full.d.ts
│       │   │   │   ├── lib.es2021.intl.d.ts
│       │   │   │   ├── lib.es2021.promise.d.ts
│       │   │   │   ├── lib.es2021.string.d.ts
│       │   │   │   ├── lib.es2021.weakref.d.ts
│       │   │   │   ├── lib.es2022.array.d.ts
│       │   │   │   ├── lib.es2022.d.ts
│       │   │   │   ├── lib.es2022.error.d.ts
│       │   │   │   ├── lib.es2022.full.d.ts
│       │   │   │   ├── lib.es2022.intl.d.ts
│       │   │   │   ├── lib.es2022.object.d.ts
│       │   │   │   ├── lib.es2022.regexp.d.ts
│       │   │   │   ├── lib.es2022.string.d.ts
│       │   │   │   ├── lib.es2023.array.d.ts
│       │   │   │   ├── lib.es2023.collection.d.ts
│       │   │   │   ├── lib.es2023.d.ts
│       │   │   │   ├── lib.es2023.full.d.ts
│       │   │   │   ├── lib.es2023.intl.d.ts
│       │   │   │   ├── lib.es2024.arraybuffer.d.ts
│       │   │   │   ├── lib.es2024.collection.d.ts
│       │   │   │   ├── lib.es2024.d.ts
│       │   │   │   ├── lib.es2024.full.d.ts
│       │   │   │   ├── lib.es2024.object.d.ts
│       │   │   │   ├── lib.es2024.promise.d.ts
│       │   │   │   ├── lib.es2024.regexp.d.ts
│       │   │   │   ├── lib.es2024.sharedmemory.d.ts
│       │   │   │   ├── lib.es2024.string.d.ts
│       │   │   │   ├── lib.es5.d.ts
│       │   │   │   ├── lib.es6.d.ts
│       │   │   │   ├── lib.esnext.array.d.ts
│       │   │   │   ├── lib.esnext.collection.d.ts
│       │   │   │   ├── lib.esnext.decorators.d.ts
│       │   │   │   ├── lib.esnext.disposable.d.ts
│       │   │   │   ├── lib.esnext.d.ts
│       │   │   │   ├── lib.esnext.error.d.ts
│       │   │   │   ├── lib.esnext.float16.d.ts
│       │   │   │   ├── lib.esnext.full.d.ts
│       │   │   │   ├── lib.esnext.intl.d.ts
│       │   │   │   ├── lib.esnext.iterator.d.ts
│       │   │   │   ├── lib.esnext.promise.d.ts
│       │   │   │   ├── lib.esnext.sharedmemory.d.ts
│       │   │   │   ├── lib.scripthost.d.ts
│       │   │   │   ├── lib.webworker.asynciterable.d.ts
│       │   │   │   ├── lib.webworker.d.ts
│       │   │   │   ├── lib.webworker.importscripts.d.ts
│       │   │   │   ├── lib.webworker.iterable.d.ts
│       │   │   │   ├── pl
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── pt-br
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── ru
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── tr
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   ├── _tsc.js
│       │   │   │   ├── tsc.js
│       │   │   │   ├── _tsserver.js
│       │   │   │   ├── tsserver.js
│       │   │   │   ├── tsserverlibrary.d.ts
│       │   │   │   ├── tsserverlibrary.js
│       │   │   │   ├── typescript.d.ts
│       │   │   │   ├── typescript.js
│       │   │   │   ├── typesMap.json
│       │   │   │   ├── _typingsInstaller.js
│       │   │   │   ├── typingsInstaller.js
│       │   │   │   ├── watchGuard.js
│       │   │   │   ├── zh-cn
│       │   │   │   │   └── diagnosticMessages.generated.json
│       │   │   │   └── zh-tw
│       │   │   │       └── diagnosticMessages.generated.json
│       │   │   ├── LICENSE.txt
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── SECURITY.md
│       │   │   └── ThirdPartyNoticeText.txt
│       │   ├── undefsafe
│       │   │   ├── example.js
│       │   │   ├── lib
│       │   │   │   └── undefsafe.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── undici-types
│       │   │   ├── agent.d.ts
│       │   │   ├── api.d.ts
│       │   │   ├── balanced-pool.d.ts
│       │   │   ├── cache.d.ts
│       │   │   ├── cache-interceptor.d.ts
│       │   │   ├── client.d.ts
│       │   │   ├── client-stats.d.ts
│       │   │   ├── connector.d.ts
│       │   │   ├── content-type.d.ts
│       │   │   ├── cookies.d.ts
│       │   │   ├── diagnostics-channel.d.ts
│       │   │   ├── dispatcher.d.ts
│       │   │   ├── env-http-proxy-agent.d.ts
│       │   │   ├── errors.d.ts
│       │   │   ├── eventsource.d.ts
│       │   │   ├── fetch.d.ts
│       │   │   ├── formdata.d.ts
│       │   │   ├── global-dispatcher.d.ts
│       │   │   ├── global-origin.d.ts
│       │   │   ├── h2c-client.d.ts
│       │   │   ├── handlers.d.ts
│       │   │   ├── header.d.ts
│       │   │   ├── index.d.ts
│       │   │   ├── interceptors.d.ts
│       │   │   ├── LICENSE
│       │   │   ├── mock-agent.d.ts
│       │   │   ├── mock-call-history.d.ts
│       │   │   ├── mock-client.d.ts
│       │   │   ├── mock-errors.d.ts
│       │   │   ├── mock-interceptor.d.ts
│       │   │   ├── mock-pool.d.ts
│       │   │   ├── package.json
│       │   │   ├── patch.d.ts
│       │   │   ├── pool.d.ts
│       │   │   ├── pool-stats.d.ts
│       │   │   ├── proxy-agent.d.ts
│       │   │   ├── readable.d.ts
│       │   │   ├── README.md
│       │   │   ├── retry-agent.d.ts
│       │   │   ├── retry-handler.d.ts
│       │   │   ├── round-robin-pool.d.ts
│       │   │   ├── snapshot-agent.d.ts
│       │   │   ├── socks5-proxy-agent.d.ts
│       │   │   ├── util.d.ts
│       │   │   ├── utility.d.ts
│       │   │   ├── webidl.d.ts
│       │   │   └── websocket.d.ts
│       │   ├── universal-user-agent
│       │   │   ├── CODE_OF_CONDUCT.md
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── index.test-d.ts
│       │   │   ├── index.test.js
│       │   │   ├── LICENSE.md
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── SECURITY.md
│       │   ├── unpipe
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── v8-compile-cache-lib
│       │   │   ├── CHANGELOG.md
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   ├── v8-compile-cache.d.ts
│       │   │   └── v8-compile-cache.js
│       │   ├── vary
│       │   │   ├── HISTORY.md
│       │   │   ├── index.js
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   └── README.md
│       │   ├── wrappy
│       │   │   ├── LICENSE
│       │   │   ├── package.json
│       │   │   ├── README.md
│       │   │   └── wrappy.js
│       │   ├── yn
│       │   │   ├── index.d.ts
│       │   │   ├── index.js
│       │   │   ├── lenient.js
│       │   │   ├── license
│       │   │   ├── package.json
│       │   │   └── readme.md
│       │   └── zod
│       │       ├── index.cjs
│       │       ├── index.d.cts
│       │       ├── index.d.ts
│       │       ├── index.js
│       │       ├── LICENSE
│       │       ├── package.json
│       │       ├── README.md
│       │       ├── src
│       │       │   ├── index.ts
│       │       │   ├── v3
│       │       │   │   ├── benchmarks
│       │       │   │   │   ├── datetime.ts
│       │       │   │   │   ├── discriminatedUnion.ts
│       │       │   │   │   ├── index.ts
│       │       │   │   │   ├── ipv4.ts
│       │       │   │   │   ├── object.ts
│       │       │   │   │   ├── primitives.ts
│       │       │   │   │   ├── realworld.ts
│       │       │   │   │   ├── string.ts
│       │       │   │   │   └── union.ts
│       │       │   │   ├── errors.ts
│       │       │   │   ├── external.ts
│       │       │   │   ├── helpers
│       │       │   │   │   ├── enumUtil.ts
│       │       │   │   │   ├── errorUtil.ts
│       │       │   │   │   ├── parseUtil.ts
│       │       │   │   │   ├── partialUtil.ts
│       │       │   │   │   ├── typeAliases.ts
│       │       │   │   │   └── util.ts
│       │       │   │   ├── index.ts
│       │       │   │   ├── locales
│       │       │   │   │   └── en.ts
│       │       │   │   ├── standard-schema.ts
│       │       │   │   ├── tests
│       │       │   │   │   ├── all-errors.test.ts
│       │       │   │   │   ├── anyunknown.test.ts
│       │       │   │   │   ├── array.test.ts
│       │       │   │   │   ├── async-parsing.test.ts
│       │       │   │   │   ├── async-refinements.test.ts
│       │       │   │   │   ├── base.test.ts
│       │       │   │   │   ├── bigint.test.ts
│       │       │   │   │   ├── branded.test.ts
│       │       │   │   │   ├── catch.test.ts
│       │       │   │   │   ├── coerce.test.ts
│       │       │   │   │   ├── complex.test.ts
│       │       │   │   │   ├── custom.test.ts
│       │       │   │   │   ├── date.test.ts
│       │       │   │   │   ├── deepmasking.test.ts
│       │       │   │   │   ├── default.test.ts
│       │       │   │   │   ├── description.test.ts
│       │       │   │   │   ├── discriminated-unions.test.ts
│       │       │   │   │   ├── enum.test.ts
│       │       │   │   │   ├── error.test.ts
│       │       │   │   │   ├── firstpartyschematypes.test.ts
│       │       │   │   │   ├── firstparty.test.ts
│       │       │   │   │   ├── function.test.ts
│       │       │   │   │   ├── generics.test.ts
│       │       │   │   │   ├── instanceof.test.ts
│       │       │   │   │   ├── intersection.test.ts
│       │       │   │   │   ├── language-server.source.ts
│       │       │   │   │   ├── language-server.test.ts
│       │       │   │   │   ├── literal.test.ts
│       │       │   │   │   ├── map.test.ts
│       │       │   │   │   ├── masking.test.ts
│       │       │   │   │   ├── mocker.test.ts
│       │       │   │   │   ├── Mocker.ts
│       │       │   │   │   ├── nan.test.ts
│       │       │   │   │   ├── nativeEnum.test.ts
│       │       │   │   │   ├── nullable.test.ts
│       │       │   │   │   ├── number.test.ts
│       │       │   │   │   ├── object-augmentation.test.ts
│       │       │   │   │   ├── object-in-es5-env.test.ts
│       │       │   │   │   ├── object.test.ts
│       │       │   │   │   ├── optional.test.ts
│       │       │   │   │   ├── parser.test.ts
│       │       │   │   │   ├── parseUtil.test.ts
│       │       │   │   │   ├── partials.test.ts
│       │       │   │   │   ├── pickomit.test.ts
│       │       │   │   │   ├── pipeline.test.ts
│       │       │   │   │   ├── preprocess.test.ts
│       │       │   │   │   ├── primitive.test.ts
│       │       │   │   │   ├── promise.test.ts
│       │       │   │   │   ├── readonly.test.ts
│       │       │   │   │   ├── record.test.ts
│       │       │   │   │   ├── recursive.test.ts
│       │       │   │   │   ├── refine.test.ts
│       │       │   │   │   ├── safeparse.test.ts
│       │       │   │   │   ├── set.test.ts
│       │       │   │   │   ├── standard-schema.test.ts
│       │       │   │   │   ├── string.test.ts
│       │       │   │   │   ├── transformer.test.ts
│       │       │   │   │   ├── tuple.test.ts
│       │       │   │   │   ├── unions.test.ts
│       │       │   │   │   ├── validations.test.ts
│       │       │   │   │   └── void.test.ts
│       │       │   │   ├── types.ts
│       │       │   │   └── ZodError.ts
│       │       │   ├── v4
│       │       │   │   ├── classic
│       │       │   │   │   ├── checks.ts
│       │       │   │   │   ├── coerce.ts
│       │       │   │   │   ├── compat.ts
│       │       │   │   │   ├── errors.ts
│       │       │   │   │   ├── external.ts
│       │       │   │   │   ├── index.ts
│       │       │   │   │   ├── iso.ts
│       │       │   │   │   ├── parse.ts
│       │       │   │   │   ├── schemas.ts
│       │       │   │   │   └── tests
│       │       │   │   │       ├── anyunknown.test.ts
│       │       │   │   │       ├── array.test.ts
│       │       │   │   │       ├── assignability.test.ts
│       │       │   │   │       ├── async-parsing.test.ts
│       │       │   │   │       ├── async-refinements.test.ts
│       │       │   │   │       ├── base.test.ts
│       │       │   │   │       ├── bigint.test.ts
│       │       │   │   │       ├── brand.test.ts
│       │       │   │   │       ├── catch.test.ts
│       │       │   │   │       ├── coalesce.test.ts
│       │       │   │   │       ├── coerce.test.ts
│       │       │   │   │       ├── continuability.test.ts
│       │       │   │   │       ├── custom.test.ts
│       │       │   │   │       ├── date.test.ts
│       │       │   │   │       ├── datetime.test.ts
│       │       │   │   │       ├── default.test.ts
│       │       │   │   │       ├── description.test.ts
│       │       │   │   │       ├── discriminated-unions.test.ts
│       │       │   │   │       ├── enum.test.ts
│       │       │   │   │       ├── error.test.ts
│       │       │   │   │       ├── error-utils.test.ts
│       │       │   │   │       ├── file.test.ts
│       │       │   │   │       ├── firstparty.test.ts
│       │       │   │   │       ├── function.test.ts
│       │       │   │   │       ├── generics.test.ts
│       │       │   │   │       ├── index.test.ts
│       │       │   │   │       ├── instanceof.test.ts
│       │       │   │   │       ├── intersection.test.ts
│       │       │   │   │       ├── json.test.ts
│       │       │   │   │       ├── lazy.test.ts
│       │       │   │   │       ├── literal.test.ts
│       │       │   │   │       ├── map.test.ts
│       │       │   │   │       ├── nan.test.ts
│       │       │   │   │       ├── nested-refine.test.ts
│       │       │   │   │       ├── nonoptional.test.ts
│       │       │   │   │       ├── nullable.test.ts
│       │       │   │   │       ├── number.test.ts
│       │       │   │   │       ├── object.test.ts
│       │       │   │   │       ├── optional.test.ts
│       │       │   │   │       ├── partial.test.ts
│       │       │   │   │       ├── pickomit.test.ts
│       │       │   │   │       ├── pipe.test.ts
│       │       │   │   │       ├── prefault.test.ts
│       │       │   │   │       ├── preprocess.test.ts
│       │       │   │   │       ├── primitive.test.ts
│       │       │   │   │       ├── promise.test.ts
│       │       │   │   │       ├── prototypes.test.ts
│       │       │   │   │       ├── readonly.test.ts
│       │       │   │   │       ├── record.test.ts
│       │       │   │   │       ├── recursive-types.test.ts
│       │       │   │   │       ├── refine.test.ts
│       │       │   │   │       ├── registries.test.ts
│       │       │   │   │       ├── set.test.ts
│       │       │   │   │       ├── standard-schema.test.ts
│       │       │   │   │       ├── stringbool.test.ts
│       │       │   │   │       ├── string-formats.test.ts
│       │       │   │   │       ├── string.test.ts
│       │       │   │   │       ├── template-literal.test.ts
│       │       │   │   │       ├── to-json-schema.test.ts
│       │       │   │   │       ├── transform.test.ts
│       │       │   │   │       ├── tuple.test.ts
│       │       │   │   │       ├── union.test.ts
│       │       │   │   │       ├── validations.test.ts
│       │       │   │   │       └── void.test.ts
│       │       │   │   ├── core
│       │       │   │   │   ├── api.ts
│       │       │   │   │   ├── checks.ts
│       │       │   │   │   ├── config.ts
│       │       │   │   │   ├── core.ts
│       │       │   │   │   ├── doc.ts
│       │       │   │   │   ├── errors.ts
│       │       │   │   │   ├── function.ts
│       │       │   │   │   ├── index.ts
│       │       │   │   │   ├── json-schema.ts
│       │       │   │   │   ├── parse.ts
│       │       │   │   │   ├── regexes.ts
│       │       │   │   │   ├── registries.ts
│       │       │   │   │   ├── schemas.ts
│       │       │   │   │   ├── standard-schema.ts
│       │       │   │   │   ├── tests
│       │       │   │   │   │   ├── index.test.ts
│       │       │   │   │   │   └── locales
│       │       │   │   │   │       ├── be.test.ts
│       │       │   │   │   │       ├── en.test.ts
│       │       │   │   │   │       ├── ru.test.ts
│       │       │   │   │   │       └── tr.test.ts
│       │       │   │   │   ├── to-json-schema.ts
│       │       │   │   │   ├── util.ts
│       │       │   │   │   ├── versions.ts
│       │       │   │   │   └── zsf.ts
│       │       │   │   ├── index.ts
│       │       │   │   ├── locales
│       │       │   │   │   ├── ar.ts
│       │       │   │   │   ├── az.ts
│       │       │   │   │   ├── be.ts
│       │       │   │   │   ├── ca.ts
│       │       │   │   │   ├── cs.ts
│       │       │   │   │   ├── de.ts
│       │       │   │   │   ├── en.ts
│       │       │   │   │   ├── eo.ts
│       │       │   │   │   ├── es.ts
│       │       │   │   │   ├── fa.ts
│       │       │   │   │   ├── fi.ts
│       │       │   │   │   ├── fr-CA.ts
│       │       │   │   │   ├── fr.ts
│       │       │   │   │   ├── he.ts
│       │       │   │   │   ├── hu.ts
│       │       │   │   │   ├── id.ts
│       │       │   │   │   ├── index.ts
│       │       │   │   │   ├── it.ts
│       │       │   │   │   ├── ja.ts
│       │       │   │   │   ├── kh.ts
│       │       │   │   │   ├── ko.ts
│       │       │   │   │   ├── mk.ts
│       │       │   │   │   ├── ms.ts
│       │       │   │   │   ├── nl.ts
│       │       │   │   │   ├── no.ts
│       │       │   │   │   ├── ota.ts
│       │       │   │   │   ├── pl.ts
│       │       │   │   │   ├── ps.ts
│       │       │   │   │   ├── pt.ts
│       │       │   │   │   ├── ru.ts
│       │       │   │   │   ├── sl.ts
│       │       │   │   │   ├── sv.ts
│       │       │   │   │   ├── ta.ts
│       │       │   │   │   ├── th.ts
│       │       │   │   │   ├── tr.ts
│       │       │   │   │   ├── ua.ts
│       │       │   │   │   ├── ur.ts
│       │       │   │   │   ├── vi.ts
│       │       │   │   │   ├── zh-CN.ts
│       │       │   │   │   └── zh-TW.ts
│       │       │   │   └── mini
│       │       │   │       ├── checks.ts
│       │       │   │       ├── coerce.ts
│       │       │   │       ├── external.ts
│       │       │   │       ├── index.ts
│       │       │   │       ├── iso.ts
│       │       │   │       ├── parse.ts
│       │       │   │       ├── schemas.ts
│       │       │   │       └── tests
│       │       │   │           ├── assignability.test.ts
│       │       │   │           ├── brand.test.ts
│       │       │   │           ├── checks.test.ts
│       │       │   │           ├── computed.test.ts
│       │       │   │           ├── error.test.ts
│       │       │   │           ├── functions.test.ts
│       │       │   │           ├── index.test.ts
│       │       │   │           ├── number.test.ts
│       │       │   │           ├── object.test.ts
│       │       │   │           ├── prototypes.test.ts
│       │       │   │           ├── recursive-types.test.ts
│       │       │   │           └── string.test.ts
│       │       │   └── v4-mini
│       │       │       └── index.ts
│       │       ├── v3
│       │       │   ├── errors.cjs
│       │       │   ├── errors.d.cts
│       │       │   ├── errors.d.ts
│       │       │   ├── errors.js
│       │       │   ├── external.cjs
│       │       │   ├── external.d.cts
│       │       │   ├── external.d.ts
│       │       │   ├── external.js
│       │       │   ├── helpers
│       │       │   │   ├── enumUtil.cjs
│       │       │   │   ├── enumUtil.d.cts
│       │       │   │   ├── enumUtil.d.ts
│       │       │   │   ├── enumUtil.js
│       │       │   │   ├── errorUtil.cjs
│       │       │   │   ├── errorUtil.d.cts
│       │       │   │   ├── errorUtil.d.ts
│       │       │   │   ├── errorUtil.js
│       │       │   │   ├── parseUtil.cjs
│       │       │   │   ├── parseUtil.d.cts
│       │       │   │   ├── parseUtil.d.ts
│       │       │   │   ├── parseUtil.js
│       │       │   │   ├── partialUtil.cjs
│       │       │   │   ├── partialUtil.d.cts
│       │       │   │   ├── partialUtil.d.ts
│       │       │   │   ├── partialUtil.js
│       │       │   │   ├── typeAliases.cjs
│       │       │   │   ├── typeAliases.d.cts
│       │       │   │   ├── typeAliases.d.ts
│       │       │   │   ├── typeAliases.js
│       │       │   │   ├── util.cjs
│       │       │   │   ├── util.d.cts
│       │       │   │   ├── util.d.ts
│       │       │   │   └── util.js
│       │       │   ├── index.cjs
│       │       │   ├── index.d.cts
│       │       │   ├── index.d.ts
│       │       │   ├── index.js
│       │       │   ├── locales
│       │       │   │   ├── en.cjs
│       │       │   │   ├── en.d.cts
│       │       │   │   ├── en.d.ts
│       │       │   │   └── en.js
│       │       │   ├── standard-schema.cjs
│       │       │   ├── standard-schema.d.cts
│       │       │   ├── standard-schema.d.ts
│       │       │   ├── standard-schema.js
│       │       │   ├── types.cjs
│       │       │   ├── types.d.cts
│       │       │   ├── types.d.ts
│       │       │   ├── types.js
│       │       │   ├── ZodError.cjs
│       │       │   ├── ZodError.d.cts
│       │       │   ├── ZodError.d.ts
│       │       │   └── ZodError.js
│       │       ├── v4
│       │       │   ├── classic
│       │       │   │   ├── checks.cjs
│       │       │   │   ├── checks.d.cts
│       │       │   │   ├── checks.d.ts
│       │       │   │   ├── checks.js
│       │       │   │   ├── coerce.cjs
│       │       │   │   ├── coerce.d.cts
│       │       │   │   ├── coerce.d.ts
│       │       │   │   ├── coerce.js
│       │       │   │   ├── compat.cjs
│       │       │   │   ├── compat.d.cts
│       │       │   │   ├── compat.d.ts
│       │       │   │   ├── compat.js
│       │       │   │   ├── errors.cjs
│       │       │   │   ├── errors.d.cts
│       │       │   │   ├── errors.d.ts
│       │       │   │   ├── errors.js
│       │       │   │   ├── external.cjs
│       │       │   │   ├── external.d.cts
│       │       │   │   ├── external.d.ts
│       │       │   │   ├── external.js
│       │       │   │   ├── index.cjs
│       │       │   │   ├── index.d.cts
│       │       │   │   ├── index.d.ts
│       │       │   │   ├── index.js
│       │       │   │   ├── iso.cjs
│       │       │   │   ├── iso.d.cts
│       │       │   │   ├── iso.d.ts
│       │       │   │   ├── iso.js
│       │       │   │   ├── parse.cjs
│       │       │   │   ├── parse.d.cts
│       │       │   │   ├── parse.d.ts
│       │       │   │   ├── parse.js
│       │       │   │   ├── schemas.cjs
│       │       │   │   ├── schemas.d.cts
│       │       │   │   ├── schemas.d.ts
│       │       │   │   └── schemas.js
│       │       │   ├── core
│       │       │   │   ├── api.cjs
│       │       │   │   ├── api.d.cts
│       │       │   │   ├── api.d.ts
│       │       │   │   ├── api.js
│       │       │   │   ├── checks.cjs
│       │       │   │   ├── checks.d.cts
│       │       │   │   ├── checks.d.ts
│       │       │   │   ├── checks.js
│       │       │   │   ├── core.cjs
│       │       │   │   ├── core.d.cts
│       │       │   │   ├── core.d.ts
│       │       │   │   ├── core.js
│       │       │   │   ├── doc.cjs
│       │       │   │   ├── doc.d.cts
│       │       │   │   ├── doc.d.ts
│       │       │   │   ├── doc.js
│       │       │   │   ├── errors.cjs
│       │       │   │   ├── errors.d.cts
│       │       │   │   ├── errors.d.ts
│       │       │   │   ├── errors.js
│       │       │   │   ├── function.cjs
│       │       │   │   ├── function.d.cts
│       │       │   │   ├── function.d.ts
│       │       │   │   ├── function.js
│       │       │   │   ├── index.cjs
│       │       │   │   ├── index.d.cts
│       │       │   │   ├── index.d.ts
│       │       │   │   ├── index.js
│       │       │   │   ├── json-schema.cjs
│       │       │   │   ├── json-schema.d.cts
│       │       │   │   ├── json-schema.d.ts
│       │       │   │   ├── json-schema.js
│       │       │   │   ├── parse.cjs
│       │       │   │   ├── parse.d.cts
│       │       │   │   ├── parse.d.ts
│       │       │   │   ├── parse.js
│       │       │   │   ├── regexes.cjs
│       │       │   │   ├── regexes.d.cts
│       │       │   │   ├── regexes.d.ts
│       │       │   │   ├── regexes.js
│       │       │   │   ├── registries.cjs
│       │       │   │   ├── registries.d.cts
│       │       │   │   ├── registries.d.ts
│       │       │   │   ├── registries.js
│       │       │   │   ├── schemas.cjs
│       │       │   │   ├── schemas.d.cts
│       │       │   │   ├── schemas.d.ts
│       │       │   │   ├── schemas.js
│       │       │   │   ├── standard-schema.cjs
│       │       │   │   ├── standard-schema.d.cts
│       │       │   │   ├── standard-schema.d.ts
│       │       │   │   ├── standard-schema.js
│       │       │   │   ├── to-json-schema.cjs
│       │       │   │   ├── to-json-schema.d.cts
│       │       │   │   ├── to-json-schema.d.ts
│       │       │   │   ├── to-json-schema.js
│       │       │   │   ├── util.cjs
│       │       │   │   ├── util.d.cts
│       │       │   │   ├── util.d.ts
│       │       │   │   ├── util.js
│       │       │   │   ├── versions.cjs
│       │       │   │   ├── versions.d.cts
│       │       │   │   ├── versions.d.ts
│       │       │   │   └── versions.js
│       │       │   ├── index.cjs
│       │       │   ├── index.d.cts
│       │       │   ├── index.d.ts
│       │       │   ├── index.js
│       │       │   ├── locales
│       │       │   │   ├── ar.cjs
│       │       │   │   ├── ar.d.cts
│       │       │   │   ├── ar.d.ts
│       │       │   │   ├── ar.js
│       │       │   │   ├── az.cjs
│       │       │   │   ├── az.d.cts
│       │       │   │   ├── az.d.ts
│       │       │   │   ├── az.js
│       │       │   │   ├── be.cjs
│       │       │   │   ├── be.d.cts
│       │       │   │   ├── be.d.ts
│       │       │   │   ├── be.js
│       │       │   │   ├── ca.cjs
│       │       │   │   ├── ca.d.cts
│       │       │   │   ├── ca.d.ts
│       │       │   │   ├── ca.js
│       │       │   │   ├── cs.cjs
│       │       │   │   ├── cs.d.cts
│       │       │   │   ├── cs.d.ts
│       │       │   │   ├── cs.js
│       │       │   │   ├── de.cjs
│       │       │   │   ├── de.d.cts
│       │       │   │   ├── de.d.ts
│       │       │   │   ├── de.js
│       │       │   │   ├── en.cjs
│       │       │   │   ├── en.d.cts
│       │       │   │   ├── en.d.ts
│       │       │   │   ├── en.js
│       │       │   │   ├── eo.cjs
│       │       │   │   ├── eo.d.cts
│       │       │   │   ├── eo.d.ts
│       │       │   │   ├── eo.js
│       │       │   │   ├── es.cjs
│       │       │   │   ├── es.d.cts
│       │       │   │   ├── es.d.ts
│       │       │   │   ├── es.js
│       │       │   │   ├── fa.cjs
│       │       │   │   ├── fa.d.cts
│       │       │   │   ├── fa.d.ts
│       │       │   │   ├── fa.js
│       │       │   │   ├── fi.cjs
│       │       │   │   ├── fi.d.cts
│       │       │   │   ├── fi.d.ts
│       │       │   │   ├── fi.js
│       │       │   │   ├── fr-CA.cjs
│       │       │   │   ├── fr-CA.d.cts
│       │       │   │   ├── fr-CA.d.ts
│       │       │   │   ├── fr-CA.js
│       │       │   │   ├── fr.cjs
│       │       │   │   ├── fr.d.cts
│       │       │   │   ├── fr.d.ts
│       │       │   │   ├── fr.js
│       │       │   │   ├── he.cjs
│       │       │   │   ├── he.d.cts
│       │       │   │   ├── he.d.ts
│       │       │   │   ├── he.js
│       │       │   │   ├── hu.cjs
│       │       │   │   ├── hu.d.cts
│       │       │   │   ├── hu.d.ts
│       │       │   │   ├── hu.js
│       │       │   │   ├── id.cjs
│       │       │   │   ├── id.d.cts
│       │       │   │   ├── id.d.ts
│       │       │   │   ├── id.js
│       │       │   │   ├── index.cjs
│       │       │   │   ├── index.d.cts
│       │       │   │   ├── index.d.ts
│       │       │   │   ├── index.js
│       │       │   │   ├── it.cjs
│       │       │   │   ├── it.d.cts
│       │       │   │   ├── it.d.ts
│       │       │   │   ├── it.js
│       │       │   │   ├── ja.cjs
│       │       │   │   ├── ja.d.cts
│       │       │   │   ├── ja.d.ts
│       │       │   │   ├── ja.js
│       │       │   │   ├── kh.cjs
│       │       │   │   ├── kh.d.cts
│       │       │   │   ├── kh.d.ts
│       │       │   │   ├── kh.js
│       │       │   │   ├── ko.cjs
│       │       │   │   ├── ko.d.cts
│       │       │   │   ├── ko.d.ts
│       │       │   │   ├── ko.js
│       │       │   │   ├── mk.cjs
│       │       │   │   ├── mk.d.cts
│       │       │   │   ├── mk.d.ts
│       │       │   │   ├── mk.js
│       │       │   │   ├── ms.cjs
│       │       │   │   ├── ms.d.cts
│       │       │   │   ├── ms.d.ts
│       │       │   │   ├── ms.js
│       │       │   │   ├── nl.cjs
│       │       │   │   ├── nl.d.cts
│       │       │   │   ├── nl.d.ts
│       │       │   │   ├── nl.js
│       │       │   │   ├── no.cjs
│       │       │   │   ├── no.d.cts
│       │       │   │   ├── no.d.ts
│       │       │   │   ├── no.js
│       │       │   │   ├── ota.cjs
│       │       │   │   ├── ota.d.cts
│       │       │   │   ├── ota.d.ts
│       │       │   │   ├── ota.js
│       │       │   │   ├── pl.cjs
│       │       │   │   ├── pl.d.cts
│       │       │   │   ├── pl.d.ts
│       │       │   │   ├── pl.js
│       │       │   │   ├── ps.cjs
│       │       │   │   ├── ps.d.cts
│       │       │   │   ├── ps.d.ts
│       │       │   │   ├── ps.js
│       │       │   │   ├── pt.cjs
│       │       │   │   ├── pt.d.cts
│       │       │   │   ├── pt.d.ts
│       │       │   │   ├── pt.js
│       │       │   │   ├── ru.cjs
│       │       │   │   ├── ru.d.cts
│       │       │   │   ├── ru.d.ts
│       │       │   │   ├── ru.js
│       │       │   │   ├── sl.cjs
│       │       │   │   ├── sl.d.cts
│       │       │   │   ├── sl.d.ts
│       │       │   │   ├── sl.js
│       │       │   │   ├── sv.cjs
│       │       │   │   ├── sv.d.cts
│       │       │   │   ├── sv.d.ts
│       │       │   │   ├── sv.js
│       │       │   │   ├── ta.cjs
│       │       │   │   ├── ta.d.cts
│       │       │   │   ├── ta.d.ts
│       │       │   │   ├── ta.js
│       │       │   │   ├── th.cjs
│       │       │   │   ├── th.d.cts
│       │       │   │   ├── th.d.ts
│       │       │   │   ├── th.js
│       │       │   │   ├── tr.cjs
│       │       │   │   ├── tr.d.cts
│       │       │   │   ├── tr.d.ts
│       │       │   │   ├── tr.js
│       │       │   │   ├── ua.cjs
│       │       │   │   ├── ua.d.cts
│       │       │   │   ├── ua.d.ts
│       │       │   │   ├── ua.js
│       │       │   │   ├── ur.cjs
│       │       │   │   ├── ur.d.cts
│       │       │   │   ├── ur.d.ts
│       │       │   │   ├── ur.js
│       │       │   │   ├── vi.cjs
│       │       │   │   ├── vi.d.cts
│       │       │   │   ├── vi.d.ts
│       │       │   │   ├── vi.js
│       │       │   │   ├── zh-CN.cjs
│       │       │   │   ├── zh-CN.d.cts
│       │       │   │   ├── zh-CN.d.ts
│       │       │   │   ├── zh-CN.js
│       │       │   │   ├── zh-TW.cjs
│       │       │   │   ├── zh-TW.d.cts
│       │       │   │   ├── zh-TW.d.ts
│       │       │   │   └── zh-TW.js
│       │       │   └── mini
│       │       │       ├── checks.cjs
│       │       │       ├── checks.d.cts
│       │       │       ├── checks.d.ts
│       │       │       ├── checks.js
│       │       │       ├── coerce.cjs
│       │       │       ├── coerce.d.cts
│       │       │       ├── coerce.d.ts
│       │       │       ├── coerce.js
│       │       │       ├── external.cjs
│       │       │       ├── external.d.cts
│       │       │       ├── external.d.ts
│       │       │       ├── external.js
│       │       │       ├── index.cjs
│       │       │       ├── index.d.cts
│       │       │       ├── index.d.ts
│       │       │       ├── index.js
│       │       │       ├── iso.cjs
│       │       │       ├── iso.d.cts
│       │       │       ├── iso.d.ts
│       │       │       ├── iso.js
│       │       │       ├── parse.cjs
│       │       │       ├── parse.d.cts
│       │       │       ├── parse.d.ts
│       │       │       ├── parse.js
│       │       │       ├── schemas.cjs
│       │       │       ├── schemas.d.cts
│       │       │       ├── schemas.d.ts
│       │       │       └── schemas.js
│       │       └── v4-mini
│       │           ├── index.cjs
│       │           ├── index.d.cts
│       │           ├── index.d.ts
│       │           └── index.js
│       ├── package.json
│       ├── README.md
│       ├── src
│       │   ├── config
│       │   │   └── env.ts
│       │   ├── events
│       │   │   ├── dispatcher.ts
│       │   │   ├── normalizer.ts
│       │   │   ├── pull-request-analyzed-handler.ts
│       │   │   ├── pull-request-opened-handler.ts
│       │   │   ├── runtime.ts
│       │   │   └── types.ts
│       │   ├── github
│       │   │   ├── app-auth.ts
│       │   │   ├── auth
│       │   │   │   └── github-app-auth.ts
│       │   │   ├── client
│       │   │   │   ├── github-contributors-client.ts
│       │   │   │   └── github-pr-files-client.ts
│       │   │   ├── github-api-client.ts
│       │   │   ├── payload.ts
│       │   │   ├── signature.ts
│       │   │   └── types
│       │   │       ├── contributor.ts
│       │   │       ├── github-auth.ts
│       │   │       └── pr-files.ts
│       │   ├── http
│       │   │   └── webhook-route.ts
│       │   ├── index.ts
│       │   ├── logging
│       │   │   └── logger.ts
│       │   ├── persistence
│       │   │   └── webhook-log-repository.ts
│       │   ├── scope
│       │   │   └── scope-extractor.ts
│       │   └── services
│       │       └── pr-persistence-service.ts
│       └── tsconfig.json
├── README.md
├── scripts
│   ├── setup-locah.sh
│   └── setup-local.sh
└── tree.md

524 directories, 2882 files
