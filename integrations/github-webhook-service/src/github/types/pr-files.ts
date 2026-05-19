import { z } from "zod";

export const githubPrFileSchema = z.object({
  filename: z.string().min(1),
  status: z.string().min(1),
  additions: z.number().int().nonnegative(),
  deletions: z.number().int().nonnegative(),
  changes: z.number().int().nonnegative()
});

export const githubPrFilesSchema = z.array(githubPrFileSchema);

export type GithubPullRequestFile = z.infer<typeof githubPrFileSchema>;
