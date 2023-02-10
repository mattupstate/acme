resource "vault_kv_secret_v2" "kratos_config" {
  mount     = vault_mount.services.path
  name      = "kratos/config"
  data_json = file("${path.module}/.secrets/kratos.json")
}
