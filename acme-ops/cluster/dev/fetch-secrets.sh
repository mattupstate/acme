#!/bin/bash

set -e
set -o pipefail

: "${OP_SECRET_ID:?OP_SECRET_ID not set or empty}"

echo "Fetching secrets from 1Password..."

SECRETS_DIR="$(dirname "${BASH_SOURCE[0]}")/002/.secrets"

op item get --format json ${OP_SECRET_ID} | \
  jq -r '.fields[] | select(.label != "notesPlain") | .label' | \
    xargs -I{} bash -c "op item get --format json ${OP_SECRET_ID} | jq '.fields[] | select(.label== \"{}\") | .value | fromjson' > ${SECRETS_DIR}/{}.json; echo Secret written to: ${SECRETS_DIR}/{}.json"
