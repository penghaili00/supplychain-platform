#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

ENV_FILE=""
WITH_MQ="false"
WAIT_TIMEOUT="180"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/start-all.sh [--env-file deploy/env/dev.env] [--with-mq]

The default env file is deploy/env/local.env when --env-file is omitted.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      ENV_FILE="$2"
      shift 2
      ;;
    --with-mq)
      WITH_MQ="true"
      shift
      ;;
    --wait-timeout)
      WAIT_TIMEOUT="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      fail "Unknown option: $1"
      ;;
  esac
done

load_env_file "$ENV_FILE"

services=()
while IFS= read -r item; do
  services+=("$item")
done < <(list_default_services)

if [[ "$WITH_MQ" == "true" ]] || [[ "${SUPPLYCHAIN_RABBITMQ_ENABLED:-false}" == "true" ]]; then
  services+=("supplychain-mq")
fi

for service in "${services[@]}"; do
  run_cmd=("${SCRIPT_DIR}/run-service.sh" --service "$service")
  if [[ -n "${SUPPLYCHAIN_ENV_FILE:-}" ]]; then
    run_cmd+=(--env-file "$SUPPLYCHAIN_ENV_FILE")
  fi
  "${run_cmd[@]}"
  port="$(service_port "$service")"
  log "Waiting for ${service} on port ${port}..."
  wait_http "http://127.0.0.1:${port}/actuator/health" "$WAIT_TIMEOUT"
done

log "All requested services are up."
