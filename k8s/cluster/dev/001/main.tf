variable "tls_cert_path" {
  type = string
}

variable "tls_key_path" {
  type = string
}

variable "ops_stack_enabled" {
  type        = bool
  description = "Wether or not to deploy the ops stack"
  default     = false
}

# NOTE: k3d comes with the metrics server pre-installed
variable "metrics_server_enabled" {
  type        = bool
  description = "To enable the k8s metrics server or not"
}

module "ns_default" {
  source = "./ns_default"
  tls_cert_path = var.tls_cert_path
  tls_key_path = var.tls_key_path
}

module "ns_management" {
  source = "./ns_management"
  tls_cert_path = var.tls_cert_path
  tls_key_path = var.tls_key_path
}

module "ns_vault" { 
  source = "./ns_vault"
  tls_cert_path = var.tls_cert_path
  tls_key_path = var.tls_key_path
}

module "ns_ops" {
  source                 = "./ns_ops"
  count                  = var.ops_stack_enabled ? 1 : 0
  tls_cert_path = var.tls_cert_path
  tls_key_path = var.tls_key_path
  metrics_server_enabled = var.metrics_server_enabled
}
