# Secrets
path "service/data/acme-web-app/*" {
  capabilities = ["read"]
}

path "database/creds/acme-data-sql-app" {
  capabilities = ["read"]
}
