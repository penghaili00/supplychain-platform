#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

ENV_FILE=""
SERVICE=""
FOREGROUND="false"
JAVA_OPTS_CLI=""

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/run-service.sh --service supplychain-gateway [--env-file deploy/env/dev.env] [--foreground]

Environment:
  SUPPLYCHAIN_JAVA_OPTS                      Common JVM options for all services.
  SUPPLYCHAIN_<SERVICE_NAME>_JAVA_OPTS       Per-service JVM options, for example:
                                      SUPPLYCHAIN_GATEWAY_JAVA_OPTS="-Xms256m -Xmx256m"

The default env file is deploy/env/local.env when --env-file is omitted.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      ENV_FILE="$2"
      shift 2
      ;;
    --service)
      SERVICE="$2"
      shift 2
      ;;
    --foreground)
      FOREGROUND="true"
      shift
      ;;
    --java-opts)
      JAVA_OPTS_CLI="$2"
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

[[ -n "$SERVICE" ]] || fail "--service is required"

load_env_file "$ENV_FILE"
ensure_command java
ensure_runtime_dirs

JAR_FILE="$(jar_path "$SERVICE" || true)"
[[ -n "$JAR_FILE" ]] || fail "Jar not found for ${SERVICE}. Run ./scripts/linux/build.sh first."

PID_FILE="$(pid_file "$SERVICE")"
LOG_FILE="$(log_dir)/${SERVICE}.log"

if [[ -f "$PID_FILE" ]]; then
  existing_pid="$(cat "$PID_FILE")"
  if [[ -n "$existing_pid" ]] && is_pid_running "$existing_pid"; then
    fail "${SERVICE} is already running with pid ${existing_pid}"
  fi
  rm -f "$PID_FILE"
fi

service_var_name="SUPPLYCHAIN_$(printf '%s' "${SERVICE#supplychain-}" | tr '[:lower:]-' '[:upper:]_')_JAVA_OPTS"
COMMON_JAVA_OPTS="${SUPPLYCHAIN_JAVA_OPTS:-}"
SERVICE_JAVA_OPTS="${!service_var_name:-}"
ALL_JAVA_OPTS="${COMMON_JAVA_OPTS} ${SERVICE_JAVA_OPTS} ${JAVA_OPTS_CLI}"
JAVA_ARGS=()

if [[ -n "${ALL_JAVA_OPTS// }" ]]; then
  # shellcheck disable=SC2206
  JAVA_ARGS=(${ALL_JAVA_OPTS})
fi

APP_ARGS=()
if [[ -n "${SPRING_PROFILES_ACTIVE:-}" ]]; then
  APP_ARGS+=("--spring.profiles.active=${SPRING_PROFILES_ACTIVE}")
fi

if [[ "$FOREGROUND" == "true" ]]; then
  cd "$PROJECT_ROOT"
  exec java "${JAVA_ARGS[@]}" -jar "$JAR_FILE" "${APP_ARGS[@]}"
fi

cd "$PROJECT_ROOT"
nohup java "${JAVA_ARGS[@]}" -jar "$JAR_FILE" "${APP_ARGS[@]}" >>"$LOG_FILE" 2>&1 &
echo $! >"$PID_FILE"

log "Started ${SERVICE}, pid=$(cat "$PID_FILE"), log=${LOG_FILE}"
