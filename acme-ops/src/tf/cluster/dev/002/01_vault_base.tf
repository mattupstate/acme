# Mounts
resource "vault_mount" "database" {
  path = "database"
  type = "database"
}

resource "vault_mount" "services" {
  type        = "kv"
  path        = "service"
  options     = { version = "2" }
  description = "Secrets for logical services running in the cluster"
}

# Policies
resource "vault_policy" "database_root_rotator" {
  name   = "rotate-root"
  policy = file("${path.module}/vault_policies/database-root-rotator.hcl")
}

#--

resource "vault_policy" "keto_app" {
  name   = "keto-app"
  policy = file("${path.module}/vault_policies/keto-app.hcl")
}

resource "vault_policy" "keto_dba" {
  name   = "keto-dba"
  policy = file("${path.module}/vault_policies/keto-dba.hcl")
}

#--

resource "vault_policy" "kratos_app" {
  name   = "kratos-app"
  policy = file("${path.module}/vault_policies/kratos-app.hcl")
}

resource "vault_policy" "kratos_dba" {
  name   = "kratos-dba"
  policy = file("${path.module}/vault_policies/kratos-dba.hcl")
}

#--

resource "vault_policy" "acme_data_sql_app" {
  name   = "acme-data-sql-app"
  policy = file("${path.module}/vault_policies/acme-data-sql-app.hcl")
}

resource "vault_policy" "acme_data_sql_dba" {
  name   = "acme-data-sql-dba"
  policy = file("${path.module}/vault_policies/acme-data-sql-dba.hcl")
}

# Authentication
resource "vault_auth_backend" "kubernetes" {
  type        = "kubernetes"
  description = "For use with Kubernetes service accounts"
}

resource "vault_kubernetes_auth_backend_config" "cluster" {
  backend         = vault_auth_backend.kubernetes.path
  kubernetes_host = "https://kubernetes.default:443"
}

resource "vault_kubernetes_auth_backend_role" "database_root_rotator" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "database-root-rotator"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = [
    "keto-database-root-rotator",
    "kratos-database-root-rotator",
    "acme-web-server-database-root-rotator"
  ]
  token_policies = [vault_policy.database_root_rotator.name]
}

#--

resource "vault_kubernetes_auth_backend_role" "keto_app" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "keto-app"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["keto-app"]
  token_policies                   = [vault_policy.keto_app.name]
}

resource "vault_kubernetes_auth_backend_role" "keto_dba" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "keto-dba"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["keto-dba-job"]
  token_policies                   = [vault_policy.keto_dba.name]
}

#--

resource "vault_kubernetes_auth_backend_role" "kratos_app" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "kratos-app"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["kratos-app"]
  token_policies                   = [vault_policy.kratos_app.name]
}

resource "vault_kubernetes_auth_backend_role" "kratos_dba" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "kratos-dba"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["kratos-dba-job"]
  token_policies                   = [vault_policy.kratos_dba.name]
}

#--

resource "vault_kubernetes_auth_backend_role" "acme_data_sql_dba" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "acme-data-sql-dba"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["acme-data-sql-migrator"]
  token_policies                   = [vault_policy.acme_data_sql_dba.name]
}

resource "vault_kubernetes_auth_backend_role" "acme_data_sql_app" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "acme-data-sql-app"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["acme-web-server"]
  token_policies                   = [vault_policy.acme_data_sql_app.name]
}
