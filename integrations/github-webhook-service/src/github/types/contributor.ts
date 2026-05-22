export interface GithubContributor {
  login: string;
  id: number;
  node_id?: string;
  avatar_url?: string;
  html_url?: string;
  contributions: number;
}
