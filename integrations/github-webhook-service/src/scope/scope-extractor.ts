export type ScopeType = "FOLDER";

export interface ExtractedScope {
  scopeType: ScopeType;
  scopeIdentifier: string;
}

export function extractScopeFromFilePath(filePath: string): ExtractedScope {
  const normalized = filePath.trim();
  const parts = normalized.split("/").filter(Boolean);

  if (parts.length <= 1) {
    return {
      scopeType: "FOLDER",
      scopeIdentifier: "__root__"
    };
  }

  return {
    scopeType: "FOLDER",
    scopeIdentifier: parts.slice(0, -1).join("/")
  };
}
