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

resource "helm_release" "keto" {
  chart     = var.keto_chart
  name      = "keto"
  namespace = "default"
  atomic    = true

  values = [
    file("${path.module}/helm_release.keto.values.yaml")
  ]

  depends_on = [
    vault_database_secret_backend_role.keto_dba,
    vault_database_secret_backend_role.keto_app
  ]
}

resource "helm_release" "kratos" {
  chart     = var.kratos_chart
  name      = "kratos"
  namespace = "default"
  atomic    = true

  values = [
    file("${path.module}/helm_release.kratos.values.yaml")
  ]

  depends_on = [
    vault_database_secret_backend_role.kratos_dba,
    vault_database_secret_backend_role.kratos_app
  ]
}
