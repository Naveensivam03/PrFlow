#!/usr/bin/env bash
set -euo pipefail

# Backward-compatible wrapper for common typo.
# Delegates to the canonical local setup script.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$SCRIPT_DIR/setup-local.sh" "$@"
