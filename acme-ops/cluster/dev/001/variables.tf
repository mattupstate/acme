
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
