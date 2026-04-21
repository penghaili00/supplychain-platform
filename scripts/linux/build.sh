#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

ENV_FILE=""
SKIP_TESTS="true"
DO_CLEAN="true"
MODULES=()

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/build.sh [--env-file deploy/env/dev.env] [--module supplychain-admin] [--no-clean] [--with-tests]

Options:
  --env-file    Load env file before build. Default: deploy/env/local.env
  --module      Build only one service module. Repeatable.
  --no-clean    Skip mvn clean.
  --with-tests  Run tests during package.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      ENV_FILE="$2"
      shift 2
      ;;
    --module)
      MODULES+=("$2")
      shift 2
      ;;
    --no-clean)
      DO_CLEAN="false"
      shift
      ;;
    --with-tests)
      SKIP_TESTS="false"
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

if [[ -x "${PROJECT_ROOT}/mvnw" ]]; then
  MVN_CMD=("${PROJECT_ROOT}/mvnw")
else
  ensure_command mvn
  MVN_CMD=(mvn)
fi

cmd=("${MVN_CMD[@]}")

if [[ ${#MODULES[@]} -gt 0 ]]; then
  maven_modules=()
  for module in "${MODULES[@]}"; do
    maven_modules+=("$(maven_module_path "$module")")
  done
  cmd+=(-pl "$(IFS=,; echo "${maven_modules[*]}")" -am)
fi

if [[ "$SKIP_TESTS" == "true" ]]; then
  cmd+=(-DskipTests)
fi
if [[ "$DO_CLEAN" == "true" ]]; then
  cmd+=(clean)
fi
cmd+=(package)

log "Running build: ${cmd[*]}"
cd "$PROJECT_ROOT"
"${cmd[@]}"

if [[ ${#MODULES[@]} -eq 0 ]]; then
  MODULES=()
  while IFS= read -r item; do
    MODULES+=("$item")
  done < <(list_default_services)
  MODULES+=("supplychain-mq")
fi

for module in "${MODULES[@]}"; do
  jar="$(jar_path "$module" || true)"
  if [[ -n "$jar" ]]; then
    log "Built ${module}: ${jar}"
  fi
done
