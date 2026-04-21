#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

ENV_FILE=""
COMPOSE_FILE="docker-compose.yml"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/compose.sh [--env-file deploy/env/local.env] [--compose-file docker-compose.yml] <compose args>

Examples:
  ./scripts/linux/compose.sh pull
  ./scripts/linux/compose.sh up -d mysql redis nacos rabbitmq
  ./scripts/linux/compose.sh --env-file deploy/env/dev.env config
  ./scripts/linux/compose.sh ps
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      ENV_FILE="$2"
      shift 2
      ;;
    --compose-file)
      COMPOSE_FILE="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      break
      ;;
  esac
done

[[ $# -gt 0 ]] || {
  usage
  exit 1
}

load_env_file "$ENV_FILE"
ensure_command docker
detect_compose

COMPOSE_FILE="$(resolve_path "$COMPOSE_FILE")"
[[ -f "$COMPOSE_FILE" ]] || fail "Compose file not found: $COMPOSE_FILE"

cd "$PROJECT_ROOT"
compose -f "$COMPOSE_FILE" "$@"
