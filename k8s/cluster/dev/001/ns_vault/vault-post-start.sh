#!/bin/sh

sleep 10

wget https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 -O /home/vault/jq
chmod +x /home/vault/jq

STATUS_FILE=/home/vault/status.json
INIT_FILE=/vault/data/init.json

vault status -format=json > $STATUS_FILE

if /home/vault/jq -e '.initialized == false' $STATUS_FILE; then
    vault operator init -n 1 -t 1 -format=json > $INIT_FILE
fi

if /home/vault/jq -e '.sealed == true' $STATUS_FILE; then
    vault operator unseal "$(/home/vault/jq -r '.unseal_keys_b64[0]' $INIT_FILE)"
fi