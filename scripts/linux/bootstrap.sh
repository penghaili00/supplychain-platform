#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

ENV_FILE=""
COMPOSE_FILE="docker-compose.yml"
SKIP_COMPOSE_UP="false"
SKIP_NACOS_IMPORT="false"
PULL_FIRST="false"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/bootstrap.sh [--env-file deploy/env/local.env] [--pull-first]

Options:
  --env-file           Load SUPPLYCHAIN_* variables before bootstrap. Default: deploy/env/local.env
  --compose-file       Override compose file path.
  --pull-first         Pull images before compose up.
  --skip-compose-up    Skip docker compose up.
  --skip-nacos-import  Skip Nacos import.
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
    --pull-first)
      PULL_FIRST="true"
      shift
      ;;
    --skip-compose-up)
      SKIP_COMPOSE_UP="true"
      shift
      ;;
    --skip-nacos-import)
      SKIP_NACOS_IMPORT="true"
      shift
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
ensure_command docker
ensure_command curl
detect_compose

COMPOSE_FILE="$(resolve_path "$COMPOSE_FILE")"
[[ -f "$COMPOSE_FILE" ]] || fail "Compose file not found: $COMPOSE_FILE"

MYSQL_PORT="${SUPPLYCHAIN_MYSQL_PORT:-3306}"
MYSQL_HOST="${SUPPLYCHAIN_MYSQL_HOST:-127.0.0.1}"
REDIS_PORT="${SUPPLYCHAIN_REDIS_PORT:-6379}"
REDIS_HOST="${SUPPLYCHAIN_REDIS_HOST:-127.0.0.1}"
ES_URL="${SUPPLYCHAIN_ES_URL:-http://127.0.0.1:9200}"
ES_USERNAME="${SUPPLYCHAIN_ES_USERNAME:-elastic}"
ES_PASSWORD="${SUPPLYCHAIN_ES_PASSWORD:-}"
RABBITMQ_PORT="${SUPPLYCHAIN_RABBITMQ_AMQP_PORT:-5672}"
RABBITMQ_HOST="${SUPPLYCHAIN_RABBITMQ_HOST:-127.0.0.1}"
NACOS_HTTP_PORT="${SUPPLYCHAIN_NACOS_HTTP_PORT:-8848}"
NACOS_BASE_URL="$(to_base_url "${SUPPLYCHAIN_NACOS_ADDR:-127.0.0.1:${NACOS_HTTP_PORT}}")"

cd "$PROJECT_ROOT"

if [[ "$PULL_FIRST" == "true" ]]; then
  log "Pulling infrastructure images..."
  compose -f "$COMPOSE_FILE" pull mysql redis elasticsearch nacos rabbitmq
fi

if [[ "$SKIP_COMPOSE_UP" != "true" ]]; then
  log "Starting infrastructure containers..."
  compose -f "$COMPOSE_FILE" up -d mysql redis elasticsearch nacos rabbitmq
fi

log "Waiting for MySQL..."
wait_tcp "$MYSQL_HOST" "$MYSQL_PORT" 180
log "Waiting for Redis..."
wait_tcp "$REDIS_HOST" "$REDIS_PORT" 180
if [[ -n "$ES_PASSWORD" ]]; then
  log "Waiting for Elasticsearch HTTP..."
  start_time="$(date +%s)"
  while true; do
    if curl -fsS -u "${ES_USERNAME}:${ES_PASSWORD}" "${ES_URL}/_cluster/health?wait_for_status=yellow&timeout=5s" >/dev/null 2>&1; then
      break
    fi
    now_time="$(date +%s)"
    if (( now_time - start_time >= 180 )); then
      fail "Timed out waiting for Elasticsearch at ${ES_URL}"
    fi
    sleep 3
  done
fi
if [[ "$SKIP_COMPOSE_UP" != "true" ]] || [[ "${SUPPLYCHAIN_RABBITMQ_ENABLED:-false}" == "true" ]]; then
  log "Waiting for RabbitMQ..."
  wait_tcp "$RABBITMQ_HOST" "$RABBITMQ_PORT" 180
fi
log "Waiting for Nacos HTTP..."
wait_http "${NACOS_BASE_URL}/nacos/" 180

if [[ "$SKIP_NACOS_IMPORT" != "true" ]]; then
  import_cmd=("${SCRIPT_DIR}/import-nacos.sh")
  if [[ -n "${SUPPLYCHAIN_ENV_FILE:-}" ]]; then
    import_cmd+=(--env-file "$SUPPLYCHAIN_ENV_FILE")
  fi
  "${import_cmd[@]}"
fi

log "Infrastructure is ready."
log "Next step: ./scripts/linux/build.sh --env-file ${SUPPLYCHAIN_ENV_FILE:-deploy/env/local.env}"
