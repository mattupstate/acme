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
