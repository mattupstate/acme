#!/bin/sh

if [[ -z "${VAULT_DATABASE}" ]]; then
    echo "VAULT_DATABASE is not set"
    exit 1
fi

if [[ -z "${VAULT_TOKEN_FILE}" ]]; then
    echo "VAULT_TOKEN_FILE is not set"
    exit 1
fi

VAULT_TOKEN="$(cat ${VAULT_TOKEN_FILE})" \
    vault write -force database/rotate-root/${VAULT_DATABASE}