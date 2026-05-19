import { z } from "zod";

const repoSchema = z.object({
  id: z.number(),
  name: z.string(),
  owner: z.object({
    id: z.number(),
    login: z.string()
  })
});

const orgSchema = z
  .object({
    id: z.number(),
    login: z.string()
  })
  .optional();

const authorSchema = z.object({
  id: z.number(),
  login: z.string(),
  name: z.string().nullable().optional(),
  avatar_url: z.string().nullable().optional()
});

const installationSchema = z.object({
  id: z.number()
});

const prSchema = z.object({
  number: z.number(),
  title: z.string(),
  body: z.string().nullable().optional(),
  updated_at: z.string(),
  created_at: z.string(),
  closed_at: z.string().nullable().optional(),
  merged_at: z.string().nullable().optional(),
  user: authorSchema
});

const pullRequestEventSchema = z.object({
  action: z.enum(["opened", "synchronize", "closed"]),
  pull_request: prSchema,
  repository: repoSchema,
  organization: orgSchema,
  installation: installationSchema.optional()
});

const pullRequestReviewEventSchema = z.object({
  action: z.literal("submitted"),
  review: z.object({
    submitted_at: z.string()
  }),
  pull_request: z.object({
    number: z.number(),
    updated_at: z.string()
  }),
  repository: repoSchema,
  organization: orgSchema,
  installation: installationSchema.optional()
});

const installationEventSchema = z.object({
  action: z.string(),
  installation: installationSchema
});

export type PullRequestEventPayload = z.infer<typeof pullRequestEventSchema>;
export type PullRequestReviewEventPayload = z.infer<typeof pullRequestReviewEventSchema>;
export type InstallationEventPayload = z.infer<typeof installationEventSchema>;

export function parsePayloadByEvent(eventName: string, payload: unknown):
  | { kind: "pull_request"; payload: PullRequestEventPayload }
  | { kind: "pull_request_review"; payload: PullRequestReviewEventPayload }
  | { kind: "installation"; payload: InstallationEventPayload }
  | { kind: "unsupported" } {
  if (eventName === "pull_request") {
    return { kind: "pull_request", payload: pullRequestEventSchema.parse(payload) };
  }

  if (eventName === "pull_request_review") {
    return { kind: "pull_request_review", payload: pullRequestReviewEventSchema.parse(payload) };
  }

  if (eventName === "installation") {
    return { kind: "installation", payload: installationEventSchema.parse(payload) };
  }

  return { kind: "unsupported" };
}
