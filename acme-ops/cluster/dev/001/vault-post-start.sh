#!/bin/sh

sleep 10

wget https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 -O /home/vault/jq
chmod +x /home/vault/jq

vault status -format=json > /home/vault/status.json

if /home/vault/jq -e '.initialized == false' /home/vault/status.json; then
    vault operator init -n 1 -t 1 -format=json > /vault/data/init.json
fi

if /home/vault/jq -e '.sealed == true' /home/vault/status.json; then
    vault operator unseal "$(/home/vault/jq -r '.unseal_keys_b64[0]' /vault/data/init.json)"
fi