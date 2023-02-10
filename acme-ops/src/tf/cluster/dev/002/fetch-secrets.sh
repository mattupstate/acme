#!/bin/bash

echo "Fetching secrets from 1Password..."

op item get --format json ${OP_SECRET_ID} | \
  jq -r '.fields[] | select(.label != "notesPlain") | .label' | \
    xargs -I{} bash -c "op item get --format json ${OP_SECRET_ID} | jq '.fields[] | select(.label== \"{}\") | .value | fromjson' > .secrets/{}.json"

echo "Secrets fetched successfully!"
