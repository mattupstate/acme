#!/bin/bash

CREDENTIALS_FILE="/vault/secrets/credentials"

if [[ -f ${CREDENTIALS_FILE} ]]; then
  export $(cat ${CREDENTIALS_FILE} | xargs)
else
  echo "${CREDENTIALS_FILE} does not exist"
  exit 1
fi

liqui() {
    liquibase \
        --classpath=/liquibase/changelog \
        --changeLogFile=changelog.yaml \
        --url=${JDBC_URL} \
        --username="${DB_USERNAME}" \
        --password="${DB_PASSWORD}" \
        --logLevel=${LOG_LEVEL} \
        --show-banner=false \
        $1
}

if type "$1" > /dev/null 2>&1; then
  ## First argument is an actual OS command. Run it
  exec "$@"
else
  if [[ "$1" == "update" ]]; then
    liqui update || exit 1
  elif [[ "$1" == "status" ]]; then
    liqui status | grep -E -e '' -e "changesets have not been applied" || exit 1
  else
    echo "Unsupported command: $1"
    exit 1
  fi
fi
