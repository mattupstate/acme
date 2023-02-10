# Secrets
path "service/data/keto/*" {
  capabilities = ["read"]
}

path "database/creds/keto-app" {
  capabilities = ["read"]
}
