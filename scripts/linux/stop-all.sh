#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/stop-all.sh [service-name]

Examples:
  ./scripts/linux/stop-all.sh
  ./scripts/linux/stop-all.sh supplychain-gateway
EOF
}

stop_service() {
  local service="$1"
  local file pid

  file="$(pid_file "$service")"
  if [[ ! -f "$file" ]]; then
    warn "No pid file found for ${service}"
    return 0
  fi

  pid="$(cat "$file")"
  if [[ -z "$pid" ]]; then
    rm -f "$file"
    warn "Empty pid file removed for ${service}"
    return 0
  fi

  if ! is_pid_running "$pid"; then
    rm -f "$file"
    warn "${service} is not running, removed stale pid file"
    return 0
  fi

  kill "$pid"
  for _ in $(seq 1 20); do
    if ! is_pid_running "$pid"; then
      rm -f "$file"
      log "Stopped ${service}"
      return 0
    fi
    sleep 1
  done

  kill -9 "$pid"
  rm -f "$file"
  warn "Force stopped ${service}"
}

if [[ $# -eq 1 ]] && [[ "$1" == "-h" || "$1" == "--help" ]]; then
  usage
  exit 0
fi

if [[ $# -gt 1 ]]; then
  usage
  exit 1
fi

ensure_runtime_dirs

if [[ $# -eq 1 ]]; then
  stop_service "$1"
  exit 0
fi

services=("supplychain-mq")
while IFS= read -r item; do
  services+=("$item")
done < <(list_default_services)

for service in "${services[@]}"; do
  stop_service "$service"
done
