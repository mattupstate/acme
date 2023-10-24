variable "vault_kubernetes_auth_backend_path" {
  type = string
}

variable "database_root_rotator_chart" {
  type = string
}

variable "deployment_rotator_chart" {
  type = string
}

variable "vault_mount_database_path" {
  type = string
}

variable "vault_services_path" {
  type = string
}

variable "secrets_file" {
  type = string
}

resource "helm_release" "kratos_postgresql" {
  repository = "https://charts.bitnami.com/bitnami"
  chart      = "postgresql"
  name       = "kratos-postgresql"
  namespace  = "default"
  version    = "12.1.7"
  atomic     = true
  timeout    = 60

  values = [
    file("${path.module}/helm_release.kratos_postgresql.values.yaml")
  ]
}

resource "vault_policy" "kratos_app" {
  name   = "kratos-app"
  policy = file("${path.module}/kratos-app.hcl")
}

resource "vault_policy" "kratos_dba" {
  name   = "kratos-dba"
  policy = file("${path.module}/kratos-dba.hcl")
}

resource "vault_kubernetes_auth_backend_role" "kratos_app" {
  backend   = var.vault_kubernetes_auth_backend_path
  role_name = "kratos-app"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["kratos-app"]
  token_policies                   = [vault_policy.kratos_app.name]
}

resource "vault_kubernetes_auth_backend_role" "kratos_dba" {
  backend   = var.vault_kubernetes_auth_backend_path
  role_name = "kratos-dba"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["kratos-dba-job"]
  token_policies                   = [vault_policy.kratos_dba.name]
}

resource "vault_kv_secret_v2" "kratos_config" {
  mount     = var.vault_services_path
  name      = "kratos/config"
  data_json = file("${var.secrets_file}")
}

resource "vault_database_secret_backend_connection" "kratos" {
  backend = var.vault_mount_database_path
  name    = "kratos"

  allowed_roles = [
    "kratos-dba",
    "kratos-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@kratos-postgresql.default.svc.cluster.local:5432/kratos"
  }

  depends_on = [
    helm_release.kratos_postgresql
  ]
}

resource "vault_database_secret_backend_role" "kratos_dba" {
  name                = "kratos-dba"
  backend             = var.vault_mount_database_path
  db_name             = vault_database_secret_backend_connection.kratos.name
  # NOTE: dba credentials last for 1 hour
  default_ttl         = 60 * 60
  max_ttl             = 60 * 60
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT kratos_dba TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE kratos_dba;"
  ]
}

resource "vault_database_secret_backend_role" "kratos_app" {
  name                = "kratos-app"
  backend             = var.vault_mount_database_path
  db_name             = vault_database_secret_backend_connection.kratos.name
  # NOTE: app credentials last for 18 days
  default_ttl         = 60 * 60 * 24 * 18
  max_ttl             = 60 * 60 * 24 * 18
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT kratos_app TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE kratos_app;"
  ]
}

resource "helm_release" "kratos_database_root_rotator" {
  chart     = var.database_root_rotator_chart
  name      = "kratos-database-root-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "vault.database"
    value = "kratos"
  }

  depends_on = [
    vault_database_secret_backend_role.kratos_dba,
    vault_database_secret_backend_role.kratos_app,
  ]
}

# NOTE: Would prefer this but couldn't get it to work. Opted for "null_resource" approach below
# resource "helm_release" "kratos" {
#   repository = "https://k8s.ory.sh/helm/charts"
#   chart      = "kratos"
#   version    = "0.36.0"
#   name       = "kratos"
#   namespace  = "default"
#   atomic     = true

#   values = [
#     file("${path.module}/helm_release.kratos.values.yaml")
#   ]

#   postrender {
#     binary_path = "${path.module}/kustomize.sh"
#     args = ["${path.module}"]
#   }

#   depends_on = [
#     helm_release.kratos_database_root_rotator,
#     vault_database_secret_backend_role.kratos_app,
#   ]
# }

# NOTE: Not ideal, but couldn't get "helm_release" above to work yet
resource "null_resource" "kratos_kustomization" {
  triggers = {
    always_run = timestamp()
  }

  provisioner "local-exec" {
    command     = "kustomize build --enable-helm ${path.module} | kubectl apply -f -"
    interpreter = ["/bin/bash", "-c"]
  }

  depends_on = [
    helm_release.kratos_database_root_rotator,
    vault_database_secret_backend_role.kratos_app,
  ]
}

resource "helm_release" "kratos_rotator" {
  # NOTE: Necessary given kratos does not support reloading of database credentials.
  chart     = var.deployment_rotator_chart
  name      = "kratos-deployment-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "deployment"
    value = "kratos"
  }

  # At 12:00 on the 1st and 15th of every month.
  set {
    name  = "schedule"
    value = "0 12 1\\,15 * *"
  }

  depends_on = [
    null_resource.kratos_kustomization
  ]
}

resource "helm_release" "kratos_self_service_ui" {
  chart      = "kratos-selfservice-ui-node"
  repository = "https://k8s.ory.sh/helm/charts"
  name       = "kratos-selfservice-ui"
  namespace  = "default"
  atomic     = true
  timeout    = 30

  values = [
    file("${path.module}/helm_release.kratos_selfservice_ui.values.yaml")
  ]
}
