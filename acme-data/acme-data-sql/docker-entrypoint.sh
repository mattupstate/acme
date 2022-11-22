#!/bin/bash

liqui() {
    liquibase \
        --classpath=/liquibase/changelog \
        --changeLogFile=changelog.yaml \
        --url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
        --username=${DB_USERNAME} \
        --password="${DB_PASSWORD}" \
        --logLevel=${LOG_LEVEL} \
        $1
}

if type "$1" > /dev/null 2>&1; then
  ## First argument is an actual OS command. Run it
  exec "$@"
else
  if [[ "$1" == "update" ]]; then
    liqui update || exit 1
  fi
  if [[ "$1" == "status" ]]; then
    liqui status | grep -E -e '' -e "changesets have not been applied" || exit 1
  fi
fi
