variable "database_root_rotator_service_account_names" {
  type = list(string)
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

resource "vault_mount" "transit" {
  type        = "transit"
  path        = "transit"
  description = "Transit encryption"
  
}

# Policies
resource "vault_policy" "database_root_rotator" {
  name   = "rotate-root"
  policy = file("${path.module}/database-root-rotator.hcl")
}

resource "vault_kubernetes_auth_backend_role" "database_root_rotator" {
  backend   = vault_auth_backend.kubernetes.path
  role_name = "database-root-rotator"

  bound_service_account_namespaces = ["default"]
  bound_service_account_names = var.database_root_rotator_service_account_names
  token_policies = [vault_policy.database_root_rotator.name]
}

resource "helm_release" "mailhog" {
  repository = "https://codecentric.github.io/helm-charts"
  chart      = "mailhog"
  name       = "mailhog"
  version    = "5.2.3"
  atomic     = true

  values = [
    file("${path.module}/helm_release.mailhog.values.yaml")
  ]
}

output "vault_kubernetes_auth_backend_path" {
  value = vault_auth_backend.kubernetes.path
}

output "vault_mount_database_path" { 
  value = vault_mount.database.path
}

output "vault_mount_services_path" {
  value = vault_mount.services.path
}
