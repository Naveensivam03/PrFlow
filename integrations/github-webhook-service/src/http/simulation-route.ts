import type { Request, Response } from "express";
import { logger } from "../logging/logger";

export async function handleSimulateInstallation(req: Request, res: Response): Promise<void> {
  logger.info("GitHub webhook simulation: initiating installation");

  try {
    // 1. Upsert the Naveensivam03 organization
    const orgs = await Bun.sql<{ id: number }[]>`
      INSERT INTO organizations (github_installation_id, github_org_id, name, plan_type, is_active)
      VALUES (133476200, 177084780, 'Naveensivam03', 'FREE', true)
      ON CONFLICT (github_org_id)
      DO UPDATE SET github_installation_id = EXCLUDED.github_installation_id, name = EXCLUDED.name
      RETURNING id
    `;

    const orgId = orgs[0]?.id;
    if (!orgId) {
      throw new Error("Failed to insert or retrieve organization");
    }

    // 2. Clear old repositories first to start fresh (optional, but let's keep them and do ON CONFLICT DO NOTHING)
    await Bun.sql`
      INSERT INTO repositories (organization_id, github_repo_id, name, default_branch, expertise_mode, is_active)
      VALUES
        (${orgId}, 1001, 'prflow-core', 'main', 'GENERAL', false),
        (${orgId}, 1002, 'github-webhook-service', 'main', 'GENERAL', false),
        (${orgId}, 1003, 'spring-api', 'main', 'GENERAL', false),
        (${orgId}, 1004, 'infra-deployments', 'main', 'GENERAL', false),
        (${orgId}, 1005, 'frontend-dashboard', 'main', 'GENERAL', false)
      ON CONFLICT (organization_id, github_repo_id)
      DO UPDATE SET name = EXCLUDED.name
    `;

    logger.info("GitHub webhook simulation: installation completed and repositories inserted", { orgId });
    res.status(200).json({
      status: "SUCCESS",
      message: "Simulation completed: Naveensivam03 organization and repositories registered.",
      data: {
        organization: "Naveensivam03",
        repositories: [
          { id: 1001, name: "prflow-core" },
          { id: 1002, name: "github-webhook-service" },
          { id: 1003, name: "spring-api" },
          { id: 1004, name: "infra-deployments" },
          { id: 1005, name: "frontend-dashboard" }
        ]
      }
    });
  } catch (error) {
    logger.error("GitHub webhook simulation installation failed", {
      error: error instanceof Error ? error.message : "unknown"
    });
    res.status(500).json({ error: "Simulation failure", details: error instanceof Error ? error.message : "unknown" });
  }
}

export async function handleSimulateSync(req: Request, res: Response): Promise<void> {
  const { repositoryIds } = req.body as { repositoryIds?: number[] };
  logger.info("GitHub webhook simulation: initiating repository synchronization", { repositoryIds });

  if (!repositoryIds || repositoryIds.length === 0) {
    res.status(400).json({ error: "Missing repositoryIds in request body" });
    return;
  }

  // Clear previous simulated sync metrics to show clean progress rising from zero!
  try {
    await Bun.sql`DELETE FROM pull_request_reviews WHERE github_review_id >= 90000`;
    await Bun.sql`DELETE FROM developer_file_expertise WHERE scope_identifier LIKE 'Comp%';`;
    await Bun.sql`DELETE FROM pull_requests WHERE github_pr_number >= 1000;`;
    await Bun.sql`DELETE FROM developers WHERE username LIKE 'dev-%';`;
  } catch (e) {
    logger.warn("Error during simulation cleanup", {
      error: e instanceof Error ? e.message : String(e)
    });
  }

  // Respond immediately that synchronization has started asynchronously
  res.status(202).json({
    status: "SUCCESS",
    message: "Simulation sync initiated asynchronously."
  });

  // Run the asynchronous ingestion simulation loop in the background!
  const runBackgroundSync = async () => {
    try {
      // 1. Simulate fetching/registering contributors (Developers)
      logger.info("Simulation Sync: Ingesting developers...");
      for (let i = 1; i <= 15; i++) {
        await Bun.sql`
          INSERT INTO developers (organization_id, github_user_id, username, display_name, avatar_url, review_capacity, reliability_score, is_active)
          VALUES (1, ${2000 + i}, ${`dev-${i}`}, ${`Developer ${i}`}, ${`https://avatar.iran.liara.run/public/${i}`}, 5, 0.9, true)
          ON CONFLICT (organization_id, github_user_id) DO NOTHING
        `;
      }
      logger.info("Simulation Sync: Developers ingested.");

      // Delay 1.5 seconds before starting PR Ingestion
      await new Promise((r) => setTimeout(r, 1500));
      logger.info("Simulation Sync: Ingesting pull requests...");
      
      // 2. Simulate fetching and persisting pull requests
      let prIdx = 1000;
      for (const repoId of repositoryIds) {
        for (let i = 1; i <= 8; i++) {
          const prNumber = prIdx++;
          await Bun.sql`
            INSERT INTO pull_requests (repository_id, author_id, github_pr_number, title, description, status, opened_at)
            VALUES (
              ${repoId},
              (SELECT id FROM developers WHERE username = ${`dev-${(i % 15) + 1}`} LIMIT 1),
              ${prNumber},
              ${`Refactor: Core optimization module #${i}`},
              'This pull request optimizes cache handling, query persistence structures, and decreases indexing latency.',
              'OPEN',
              CURRENT_TIMESTAMP
            )
            ON CONFLICT (repository_id, github_pr_number) DO NOTHING
          `;
        }
      }
      logger.info("Simulation Sync: Pull requests ingested.");

      // Delay 2 seconds before building file expertise memory graphs
      await new Promise((r) => setTimeout(r, 2000));
      logger.info("Simulation Sync: Building developer file expertise...");

      // 3. Simulate Expertise Mapping
      for (const repoId of repositoryIds) {
        for (let i = 1; i <= 20; i++) {
          await Bun.sql`
            INSERT INTO developer_file_expertise (developer_id, repository_id, file_path, scope_identifier, expertise_score)
            VALUES (
              (SELECT id FROM developers ORDER BY id LIMIT 1 OFFSET ${i % 12}),
              ${repoId},
              ${`src/components/Component_${i}.tsx`},
              ${`Component_${i}`},
              ${0.4 + (i % 5) * 0.12}
            )
            ON CONFLICT (developer_id, repository_id, file_path) DO NOTHING
          `;
        }
      }
      logger.info("Simulation Sync: Expertise graphs built.");

      // Delay 2 seconds before creating pull request reviews history
      await new Promise((r) => setTimeout(r, 2000));
      logger.info("Simulation Sync: Ingesting pull request reviews...");

      // 4. Simulate pull request reviews
      let reviewIdx = 90000;
      for (const repoId of repositoryIds) {
        for (let i = 1; i <= 10; i++) {
          const reviewId = reviewIdx++;
          await Bun.sql`
            INSERT INTO pull_request_reviews (pull_request_id, reviewer_id, github_review_id, review_state, review_body, review_submitted_at)
            VALUES (
              (SELECT id FROM pull_requests WHERE repository_id = ${repoId} LIMIT 1 OFFSET ${i % 5}),
              (SELECT id FROM developers ORDER BY id DESC LIMIT 1 OFFSET ${i % 8}),
              ${reviewId},
              'APPROVED',
              'Stunning implementation! Very clean, fast database writes and flawless design tokens integration.',
              CURRENT_TIMESTAMP
            )
            ON CONFLICT (github_review_id) DO NOTHING
          `;
        }
      }
      logger.info("Simulation Sync: Ingested pull request reviews. Ingestion fully complete!");
    } catch (err) {
      logger.error("Error running background sync simulation", {
        error: err instanceof Error ? err.message : String(err)
      });
    }
  };

  void runBackgroundSync();
}
