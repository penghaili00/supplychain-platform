#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RUNTIME_ROOT_DEFAULT="${PROJECT_ROOT}/.runtime"
DEFAULT_LOCAL_ENV_REL="deploy/env/local.env"

log() {
  printf '[%s] %s\n' "$(date '+%F %T')" "$*"
}

warn() {
  printf '[%s] WARN: %s\n' "$(date '+%F %T')" "$*" >&2
}

fail() {
  printf '[%s] ERROR: %s\n' "$(date '+%F %T')" "$*" >&2
  exit 1
}

ensure_command() {
  command -v "$1" >/dev/null 2>&1 || fail "Missing required command: $1"
}

resolve_path() {
  local input="$1"
  if [[ -z "$input" ]]; then
    return 1
  fi
  if [[ "$input" = /* ]]; then
    printf '%s\n' "$input"
  else
    printf '%s\n' "${PROJECT_ROOT}/${input}"
  fi
}

load_env_file() {
  local env_file="${1:-}"

  if [[ -z "$env_file" ]]; then
    env_file="${PROJECT_ROOT}/${DEFAULT_LOCAL_ENV_REL}"
  else
    env_file="$(resolve_path "$env_file")"
  fi

  [[ -f "$env_file" ]] || fail "Env file not found: $env_file"

  set -a
  # shellcheck disable=SC1090
  source "$env_file"
  set +a

  export SUPPLYCHAIN_ENV_FILE="$env_file"
  log "Loaded env file: $env_file"
}

detect_compose() {
  if docker compose version >/dev/null 2>&1; then
    COMPOSE_BIN=(docker compose)
    return
  fi

  if command -v docker-compose >/dev/null 2>&1; then
    COMPOSE_BIN=(docker-compose)
    return
  fi

  fail "Docker Compose was not found. Install docker compose plugin or docker-compose."
}

compose() {
  if [[ -n "${SUPPLYCHAIN_ENV_FILE:-}" ]]; then
    "${COMPOSE_BIN[@]}" --env-file "$SUPPLYCHAIN_ENV_FILE" "$@"
    return
  fi

  "${COMPOSE_BIN[@]}" "$@"
}

to_base_url() {
  local address="$1"
  if [[ "$address" == http://* || "$address" == https://* ]]; then
    printf '%s\n' "${address%/}"
  else
    printf 'http://%s\n' "${address%/}"
  fi
}

wait_tcp() {
  local host="$1"
  local port="$2"
  local timeout="${3:-180}"
  local start now

  start="$(date +%s)"
  while true; do
    if (echo >"/dev/tcp/${host}/${port}") >/dev/null 2>&1; then
      return 0
    fi

    now="$(date +%s)"
    if (( now - start >= timeout )); then
      fail "Timed out waiting for ${host}:${port}"
    fi

    sleep 3
  done
}

wait_http() {
  local url="$1"
  local timeout="${2:-180}"
  local start now

  ensure_command curl

  start="$(date +%s)"
  while true; do
    if curl -fsS --max-time 5 "$url" >/dev/null 2>&1; then
      return 0
    fi

    now="$(date +%s)"
    if (( now - start >= timeout )); then
      fail "Timed out waiting for ${url}"
    fi

    sleep 3
  done
}

list_default_services() {
  printf '%s\n' \
    "supplychain-service-provider" \
    "supplychain-admin" \
    "supplychain-api" \
    "supplychain-gateway" \
    "supplychain-task"
}

module_dir() {
  case "$1" in
    supplychain-service-provider) printf '%s\n' "${PROJECT_ROOT}/supplychain-service/supplychain-service-provider" ;;
    supplychain-admin) printf '%s\n' "${PROJECT_ROOT}/supplychain-admin" ;;
    supplychain-api) printf '%s\n' "${PROJECT_ROOT}/supplychain-api" ;;
    supplychain-gateway) printf '%s\n' "${PROJECT_ROOT}/supplychain-gateway" ;;
    supplychain-task) printf '%s\n' "${PROJECT_ROOT}/supplychain-task" ;;
    supplychain-mq) printf '%s\n' "${PROJECT_ROOT}/supplychain-mq" ;;
    *) fail "Unsupported service name: $1" ;;
  esac
}

maven_module_path() {
  case "$1" in
    supplychain-service-provider) printf '%s\n' "supplychain-service/supplychain-service-provider" ;;
    supplychain-admin) printf '%s\n' "supplychain-admin" ;;
    supplychain-api) printf '%s\n' "supplychain-api" ;;
    supplychain-gateway) printf '%s\n' "supplychain-gateway" ;;
    supplychain-task) printf '%s\n' "supplychain-task" ;;
    supplychain-mq) printf '%s\n' "supplychain-mq" ;;
    *) fail "Unsupported module name: $1" ;;
  esac
}

jar_path() {
  local module_path
  module_path="$(module_dir "$1")"
  find "${module_path}/target" -maxdepth 1 -type f -name '*.jar' ! -name 'original-*.jar' | sort | head -n 1
}

runtime_root() {
  printf '%s\n' "${SUPPLYCHAIN_RUNTIME_ROOT:-$RUNTIME_ROOT_DEFAULT}"
}

log_dir() {
  printf '%s\n' "${SUPPLYCHAIN_LOG_DIR:-$(runtime_root)/logs}"
}

pid_dir() {
  printf '%s\n' "${SUPPLYCHAIN_PID_DIR:-$(runtime_root)/pids}"
}

ensure_runtime_dirs() {
  mkdir -p "$(runtime_root)" "$(log_dir)" "$(pid_dir)"
}

pid_file() {
  printf '%s\n' "$(pid_dir)/$1.pid"
}

service_port() {
  case "$1" in
    supplychain-service-provider) printf '%s\n' "${SUPPLYCHAIN_SERVICE_PROVIDER_PORT:-8090}" ;;
    supplychain-admin) printf '%s\n' "${SUPPLYCHAIN_ADMIN_PORT:-8081}" ;;
    supplychain-api) printf '%s\n' "${SUPPLYCHAIN_API_PORT:-8082}" ;;
    supplychain-gateway) printf '%s\n' "${SUPPLYCHAIN_GATEWAY_PORT:-9000}" ;;
    supplychain-task) printf '%s\n' "${SUPPLYCHAIN_TASK_PORT:-8083}" ;;
    supplychain-mq) printf '%s\n' "${SUPPLYCHAIN_MQ_PORT:-8084}" ;;
    *) fail "Unsupported service name: $1" ;;
  esac
}

is_pid_running() {
  local pid="$1"
  kill -0 "$pid" >/dev/null 2>&1
}
