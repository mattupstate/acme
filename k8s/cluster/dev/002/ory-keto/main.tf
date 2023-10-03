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

resource "helm_release" "keto_postgresql" {
  repository = "https://charts.bitnami.com/bitnami"
  chart      = "postgresql"
  name       = "keto-postgresql"
  namespace  = "default"
  version    = "12.1.7"
  atomic     = true
  timeout    = 60

  values = [
    file("${path.module}/helm_release.keto_postgresql.values.yaml")
  ]
}

resource "vault_policy" "keto_app" {
  name   = "keto-app"
  policy = file("${path.module}/keto-app.hcl")
}

resource "vault_policy" "keto_dba" {
  name   = "keto-dba"
  policy = file("${path.module}/keto-dba.hcl")
}

resource "vault_kubernetes_auth_backend_role" "keto_app" {
  backend   = var.vault_kubernetes_auth_backend_path
  role_name = "keto-app"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["keto-app"]
  token_policies                   = [vault_policy.keto_app.name]
}

resource "vault_kubernetes_auth_backend_role" "keto_dba" {
  backend   = var.vault_kubernetes_auth_backend_path
  role_name = "keto-dba"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names      = ["keto-dba-job"]
  token_policies                   = [vault_policy.keto_dba.name]
}

resource "vault_database_secret_backend_connection" "keto" {
  backend = var.vault_mount_database_path
  name    = "keto"

  allowed_roles = [
    "keto-dba",
    "keto-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@keto-postgresql.default.svc.cluster.local:5432/keto"
  }

  depends_on = [
    helm_release.keto_postgresql,
    vault_kubernetes_auth_backend_role.keto_dba,
    vault_kubernetes_auth_backend_role.keto_app,
  ]
}

resource "vault_database_secret_backend_role" "keto_dba" {
  name    = "keto-dba"
  backend = var.vault_mount_database_path
  db_name = vault_database_secret_backend_connection.keto.name
  # NOTE: dba credentials last for 1 hour
  default_ttl = 60 * 60
  max_ttl     = 60 * 60
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT keto_dba TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE keto_dba;"
  ]
}

resource "vault_database_secret_backend_role" "keto_app" {
  name    = "keto-app"
  backend = var.vault_mount_database_path
  db_name = vault_database_secret_backend_connection.keto.name
  # NOTE: app credentials last for 18 days
  default_ttl = 60 * 60 * 24 * 18
  max_ttl     = 60 * 60 * 24 * 18
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT keto_app TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE keto_app;"
  ]
}

resource "helm_release" "keto_database_root_rotator" {
  chart     = var.database_root_rotator_chart
  name      = "keto-database-root-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "vault.database"
    value = "keto"
  }

  depends_on = [
    vault_database_secret_backend_role.keto_dba,
    vault_database_secret_backend_role.keto_app
  ]
}

resource "kubernetes_secret" "keto" {
  # NOTE: This is required by the chart, but the values are overriden by the custom image entrypoint
  metadata {
    namespace = "default"
    name = "keto"
  }

  data = {
    dsn = "placeholder"
  }

  type = "kubernetes.io/generic"
}

# resource "helm_release" "keto" {
#   repository = "https://k8s.ory.sh/helm/charts"
#   chart      = "keto"
#   version    = "0.36.0"
#   name       = "keto"
#   namespace  = "default"
#   atomic     = true

#   values = [
#     file("${path.module}/helm_release.keto.values.yaml")
#   ]

#   depends_on = [
#     kubernetes_secret.keto,
#     helm_release.keto_database_root_rotator,
#     vault_database_secret_backend_role.keto_app,
#   ]
# }

# NOTE: This is to get around the limitations of the Keto Helm chart
resource "null_resource" "keto_kustomization" {
  triggers = {
    always_run = "${timestamp()}"
  }
  
  provisioner "local-exec" {
   command = "kustomize build --enable-helm ${path.module} | kubectl apply -f -"
   interpreter = [ "/bin/bash", "-c" ]
  }

  depends_on = [
    helm_release.keto_database_root_rotator,
    vault_database_secret_backend_role.keto_app,
  ]
}

resource "helm_release" "keto_rotator" {
  # NOTE: Necessary given keto does not support reloading of database credentials.
  chart     = var.deployment_rotator_chart
  name      = "keto-deployment-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "deployment"
    value = "keto"
  }

  # At 12:00 on the 1st and 15th of every month.
  set {
    name  = "schedule"
    value = "0 12 1\\,15 * *"
  }

  depends_on = [
    null_resource.keto_kustomization
  ]
}
