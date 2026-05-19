import { z } from "zod";

export const installationAccessTokenResponseSchema = z.object({
  token: z.string().min(1),
  expires_at: z.string().min(1),
  permissions: z.record(z.string()).optional(),
  repositories: z.array(z.object({ id: z.number(), full_name: z.string() })).optional()
});

export type InstallationAccessTokenResponse = z.infer<typeof installationAccessTokenResponseSchema>;
