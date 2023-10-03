#!/bin/bash

set -e
set -o pipefail

: "${OP_SECRET_ID:?OP_SECRET_ID not set or empty}"

SECRETS_DIR=".secrets"

mkdir -p ${SECRETS_DIR}

op item get --format json ${OP_SECRET_ID} | \
  jq -r '.fields[] | select(.label != "notesPlain") | .label' | \
    xargs -I{} bash -c "op item get --format json ${OP_SECRET_ID} | jq '.fields[] | select(.label== \"{}\") | .value | fromjson' > ${SECRETS_DIR}/{}.json"
