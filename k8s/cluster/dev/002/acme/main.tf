variable "vault_kubernetes_auth_backend_path" {
  type = string
}

variable "vault_mount_database_path" {
  type = string
}

variable "database_root_rotator_chart" {
  type = string
}

resource "helm_release" "acme_postgresql" {
  repository = "https://charts.bitnami.com/bitnami"
  chart      = "postgresql"
  name       = "acme-postgresql"
  namespace  = "default"
  version    = "12.1.7"
  atomic     = true
  timeout    = 60

  values = [
    file("${path.module}/helm_release.acme_postgresql.values.yaml")
  ]
}

resource "vault_policy" "acme_data_sql_app" {
  name   = "acme-data-sql-app"
  policy = file("${path.module}/acme-data-sql-app.hcl")
}

resource "vault_policy" "acme_data_sql_dba" {
  name   = "acme-data-sql-dba"
  policy = file("${path.module}/acme-data-sql-dba.hcl")
}

resource "vault_kubernetes_auth_backend_role" "acme_data_sql_dba" {
  backend   = var.vault_kubernetes_auth_backend_path
  role_name = "acme-data-sql-dba"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["acme-data-sql-migrator"]
  token_policies                   = [vault_policy.acme_data_sql_dba.name]
}

resource "vault_kubernetes_auth_backend_role" "acme_data_sql_app" {
  backend   = var.vault_kubernetes_auth_backend_path
  role_name = "acme-data-sql-app"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["acme-web-api", "acme-web-app"]
  token_policies                   = [vault_policy.acme_data_sql_app.name]
}


resource "vault_database_secret_backend_connection" "acme" {
  backend = var.vault_mount_database_path
  name    = "acme-web-api"

  allowed_roles = [
    "acme-data-sql-dba",
    "acme-data-sql-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@acme-postgresql.default.svc.cluster.local:5432/acme"
  }

  depends_on = [
    helm_release.acme_postgresql
  ]
}

resource "vault_database_secret_backend_connection" "acme_app_web_htmx" {
  backend = var.vault_mount_database_path
  name    = "acme-web-app"

  allowed_roles = [
    "acme-data-sql-dba",
    "acme-data-sql-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@acme-postgresql.default.svc.cluster.local:5432/acme"
  }

  depends_on = [
    helm_release.acme_postgresql
  ]
}

resource "vault_database_secret_backend_role" "acme_data_sql_dba" {
  name                = "acme-data-sql-dba"
  backend             = var.vault_mount_database_path
  db_name             = vault_database_secret_backend_connection.acme.name
  # NOTE: dba credentials last for 1 hour
  default_ttl         = 60 * 60
  max_ttl             = 60 * 60
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT acme_data_sql_dba TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE acme_data_sql_dba;"
  ]
}

resource "vault_database_secret_backend_role" "acme_data_sql_app" {
  name                = "acme-data-sql-app"
  backend             = var.vault_mount_database_path
  db_name             = vault_database_secret_backend_connection.acme.name
  # NOTE: app credentials last for 18 days
  default_ttl         = 60 * 60 * 24 * 18
  max_ttl             = 60 * 60 * 24 * 18
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT acme_data_sql_app TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE acme_data_sql_app;"
  ]
}

resource "vault_kv_secret_v2" "session_keys" {
  mount               = "service"
  name                = "acme-web-app/sessions"
  data_json = jsonencode(
    {
      encryptionKey = "cdcb243b65e5e6095922ca5eae329239",
      signingKey = "3e3371b62c7a3da2f6ddb835e78bff4c"
    }
  )
}


resource "helm_release" "acme_database_root_rotator" {
  chart     = var.database_root_rotator_chart
  name      = "acme-database-root-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "vault.database"
    value = "acme"
  }

  depends_on = [
    vault_database_secret_backend_role.acme_data_sql_dba
  ]
}


