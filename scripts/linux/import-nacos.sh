#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=common.sh
source "${SCRIPT_DIR}/common.sh"

ENV_FILE=""
SERVER_ADDR=""
USERNAME=""
PASSWORD=""
NAMESPACE_ID=""
NAMESPACE_NAME=""
GROUP_NAME=""

usage() {
  cat <<'EOF'
Usage:
  ./scripts/linux/import-nacos.sh [--env-file deploy/env/dev.env] [--server-addr 127.0.0.1:8848]

Options:
  --env-file        Load SUPPLYCHAIN_* variables before import. Default: deploy/env/local.env
  --server-addr     Override SUPPLYCHAIN_NACOS_ADDR.
  --username        Override SUPPLYCHAIN_NACOS_USERNAME.
  --password        Override SUPPLYCHAIN_NACOS_PASSWORD.
  --namespace-id    Override SUPPLYCHAIN_NACOS_NAMESPACE.
  --namespace-name  Override SUPPLYCHAIN_NACOS_NAMESPACE_NAME.
  --group           Override SUPPLYCHAIN_NACOS_GROUP.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      ENV_FILE="$2"
      shift 2
      ;;
    --server-addr)
      SERVER_ADDR="$2"
      shift 2
      ;;
    --username)
      USERNAME="$2"
      shift 2
      ;;
    --password)
      PASSWORD="$2"
      shift 2
      ;;
    --namespace-id)
      NAMESPACE_ID="$2"
      shift 2
      ;;
    --namespace-name)
      NAMESPACE_NAME="$2"
      shift 2
      ;;
    --group)
      GROUP_NAME="$2"
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
ensure_command curl

SERVER_ADDR="${SERVER_ADDR:-${SUPPLYCHAIN_NACOS_ADDR:-127.0.0.1:8848}}"
USERNAME="${USERNAME:-${SUPPLYCHAIN_NACOS_USERNAME:-nacos}}"
PASSWORD="${PASSWORD:-${SUPPLYCHAIN_NACOS_PASSWORD:-nacos}}"
NAMESPACE_ID="${NAMESPACE_ID:-${SUPPLYCHAIN_NACOS_NAMESPACE:-supplychain_local}}"
NAMESPACE_NAME="${NAMESPACE_NAME:-${SUPPLYCHAIN_NACOS_NAMESPACE_NAME:-$NAMESPACE_ID}}"
GROUP_NAME="${GROUP_NAME:-${SUPPLYCHAIN_NACOS_GROUP:-DEFAULT_GROUP}}"
BASE_URL="$(to_base_url "$SERVER_ADDR")"
CONFIG_DIR="${PROJECT_ROOT}/nacos"
ACCESS_TOKEN=""

[[ -d "$CONFIG_DIR" ]] || fail "Nacos config directory not found: $CONFIG_DIR"

curl_with_status() {
  local output_file="$1"
  shift
  curl -sS -o "$output_file" -w '%{http_code}' "$@"
}

login_nacos() {
  local body_file status body token
  body_file="$(mktemp)"

  status="$(curl_with_status "$body_file" \
    -X POST "${BASE_URL}/nacos/v1/auth/users/login" \
    --data-urlencode "username=${USERNAME}" \
    --data-urlencode "password=${PASSWORD}")"

  body="$(cat "$body_file")"

  if [[ "$status" == "200" ]]; then
    token="$(printf '%s' "$body" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')"
    if [[ -n "$token" ]]; then
      ACCESS_TOKEN="$token"
      log "Nacos login succeeded."
      rm -f "$body_file"
      return 0
    fi
  fi

  status="$(curl_with_status "$body_file" -G "${BASE_URL}/nacos/v1/console/namespaces")"
  if [[ "$status" == "200" ]]; then
    ACCESS_TOKEN=""
    log "Nacos anonymous access is available. Skipping login."
    rm -f "$body_file"
    return 0
  fi

  fail "Failed to login to Nacos. Response: $body"
}

ensure_namespace() {
  local body_file status body auth_args=()

  [[ "$NAMESPACE_ID" != "public" ]] || return 0

  body_file="$(mktemp)"

  if [[ -n "$ACCESS_TOKEN" ]]; then
    auth_args=(--data-urlencode "accessToken=${ACCESS_TOKEN}")
    status="$(curl_with_status "$body_file" -G "${BASE_URL}/nacos/v1/console/namespaces" "${auth_args[@]}")"
  else
    status="$(curl_with_status "$body_file" -G "${BASE_URL}/nacos/v1/console/namespaces")"
  fi

  body="$(cat "$body_file")"
  if [[ "$status" == "200" ]] && [[ "$body" == *"\"namespace\":\"${NAMESPACE_ID}\""* || "$body" == *"\"namespaceShowName\":\"${NAMESPACE_NAME}\""* ]]; then
    rm -f "$body_file"
    return 0
  fi

  auth_args=()
  if [[ -n "$ACCESS_TOKEN" ]]; then
    auth_args=(--data-urlencode "accessToken=${ACCESS_TOKEN}")
  fi

  status="$(curl_with_status "$body_file" \
    -X POST "${BASE_URL}/nacos/v1/console/namespaces" \
    "${auth_args[@]}" \
    --data-urlencode "customNamespaceId=${NAMESPACE_ID}" \
    --data-urlencode "namespaceName=${NAMESPACE_NAME}" \
    --data-urlencode "namespaceDesc=SupplyChain multi-env namespace")"

  body="$(cat "$body_file")"
  if [[ "$status" != "200" ]] || [[ "$body" != "true" ]]; then
    fail "Failed to ensure namespace ${NAMESPACE_ID}. Response: $body"
  fi

  rm -f "$body_file"
  log "Created Nacos namespace: ${NAMESPACE_ID}"
}

import_file() {
  local file="$1"
  local body_file status body auth_args=()

  body_file="$(mktemp)"

  if [[ -n "$ACCESS_TOKEN" ]]; then
    auth_args=(--data-urlencode "accessToken=${ACCESS_TOKEN}")
  fi

  request_args=(
    -X POST "${BASE_URL}/nacos/v1/cs/configs"
    "${auth_args[@]}"
    --data-urlencode "dataId=$(basename "$file")"
    --data-urlencode "group=${GROUP_NAME}"
    --data-urlencode "type=yaml"
    --data-urlencode "content@${file}"
  )

  if [[ "$NAMESPACE_ID" != "public" ]]; then
    request_args+=(--data-urlencode "tenant=${NAMESPACE_ID}")
  fi

  status="$(curl_with_status "$body_file" "${request_args[@]}")"

  body="$(cat "$body_file")"
  if [[ "$status" != "200" ]] || [[ "$body" != "true" ]]; then
    fail "Failed to import $(basename "$file"). Response: $body"
  fi

  rm -f "$body_file"
  log "Imported $(basename "$file")"
}

login_nacos
ensure_namespace

shopt -s nullglob
files=("${CONFIG_DIR}"/*.yml)
shopt -u nullglob
[[ ${#files[@]} -gt 0 ]] || fail "No YAML files found under ${CONFIG_DIR}"

for file in "${files[@]}"; do
  import_file "$file"
done

log "Nacos import completed for namespace=${NAMESPACE_ID}, group=${GROUP_NAME}"
