FROM liquibase/liquibase:4.19.0
COPY src/main/resources/db /liquibase

USER root
RUN  apt-get update -y && \
  apt-get install --no-install-recommends -y postgresql-client && \
  rm -rf /var/lib/apt/lists/*

USER liquibase

COPY --chown=liquibase:liquibase docker-entrypoint.sh /liquibase/

ENV JDBC_URL="jdbc:postgresql://acme-postgresql.default.svc.cluster.local:5432/acme" \
    LOG_LEVEL=info

ENTRYPOINT ["/liquibase/docker-entrypoint.sh"]
CMD ["update"]
