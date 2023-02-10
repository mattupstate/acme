resource "helm_release" "keto_database_root_rotator" {
  chart     = "../../../../../../acme-helm-charts/database-root-rotator"
  name      = "keto-database-root-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "vault.database"
    value = "keto"
  }

  depends_on = [
    vault_database_secret_backend_connection.keto
  ]
}

resource "helm_release" "kratos_database_root_rotator" {
  chart     = "../../../../../../acme-helm-charts/database-root-rotator"
  name      = "kratos-database-root-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "vault.database"
    value = "kratos"
  }

  depends_on = [
    vault_database_secret_backend_connection.kratos
  ]
}

resource "helm_release" "acme_database_root_rotator" {
  chart     = "../../../../../../acme-helm-charts/database-root-rotator"
  name      = "acme-database-root-rotator"
  namespace = "default"
  atomic    = true
  timeout   = 30

  set {
    name  = "vault.database"
    value = "acme"
  }

  depends_on = [
    vault_database_secret_backend_connection.acme
  ]
}


