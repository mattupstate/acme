FROM liquibase/liquibase:4.17.2
COPY src/main/resources/db /liquibase

USER root
RUN  apt-get update -y && \
  apt-get install --no-install-recommends -y postgresql-client && \
  rm -rf /var/lib/apt/lists/*

USER liquibase

COPY --chown=liquibase:liquibase docker-entrypoint.sh /liquibase/

ENV DB_HOST=postgresql \
    DB_PORT=5432 \
    DB_NAME=acme \
    DB_USERNAME=acme \
    DB_PASSWORD=acme \
    LOG_LEVEL=info

CMD ["update"]