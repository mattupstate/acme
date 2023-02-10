# Secrets
path "service/data/acme-web-server/*" {
  capabilities = ["read"]
}

path "database/creds/acme-data-sql-app" {
  capabilities = ["read"]
}
