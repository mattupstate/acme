module "common" {
  source = "./common"
}

module "keto" {
  source = "./ory-keto"
  vault_kubernetes_auth_backend_path = module.common.vault_kubernetes_auth_backend_path
  vault_mount_database_path = module.common.vault_mount_database_path
  database_root_rotator_chart = var.database_root_rotator_chart
  deployment_rotator_chart = var.deployment_rotator_chart
}

module "kratos" { 
  source = "./ory-kratos"
  vault_kubernetes_auth_backend_path = module.common.vault_kubernetes_auth_backend_path
  vault_mount_database_path = module.common.vault_mount_database_path
  vault_services_path = module.common.vault_mount_services_path
  database_root_rotator_chart = var.database_root_rotator_chart
  deployment_rotator_chart = var.deployment_rotator_chart
  secrets_file = "${path.module}/.secrets/kratos.json"
}

module "acme" {
  source = "./acme"
  vault_kubernetes_auth_backend_path = module.common.vault_kubernetes_auth_backend_path
  vault_mount_database_path = module.common.vault_mount_database_path
  database_root_rotator_chart = var.database_root_rotator_chart
}