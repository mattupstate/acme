#!/bin/sh

sleep 10

wget https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 -O /home/vault/jq
chmod +x /home/vault/jq

vault operator init -n 1 -t 1 -format=json > /home/vault/init.json

vault operator unseal "$(/home/vault/jq -r '.unseal_keys_b64[0]' /home/vault/init.json)"