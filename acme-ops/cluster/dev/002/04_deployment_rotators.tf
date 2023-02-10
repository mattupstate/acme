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
    helm_release.keto
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
    helm_release.kratos
  ]
}

