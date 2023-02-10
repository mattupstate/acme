# Secrets
path "service/data/kratos/*" {
  capabilities = ["read"]
}

path "database/creds/kratos-app" {
  capabilities = ["read"]
}
