variable "vault_root_token" {
  type        = string
  description = "The Vault root token"
  default     = "root"
}

variable "kubectx" {
  type        = string
  description = "The kubectl context to interact with"
  default     = "minikube"
}

variable "keto_chart" {
  type        = string
  description = "Path to local keto chart"
  default     = "../../../../vendor/ory-helm-charts/helm/charts/keto"
}

variable "kratos_chart" {
  type        = string
  description = "Path to local kratos chart"
  default     = "../../../../vendor/ory-helm-charts/helm/charts/kratos"
}

variable "database_root_rotator_chart" {
  type        = string
  description = "Path to local database root rotator chart"
  default     = "../../../../acme-helm-charts/database-root-rotator"
}

variable "deployment_rotator_chart" {
  type        = string
  description = "Path to local deployment rotator chart"
  default     = "../../../../acme-helm-charts/deployment-rotator"
}
