terraform {
  required_version = ">= 1.3.0"

  required_providers {
    helm = {
      version = ">= 2.7.0"
    }
    kubernetes = {
      version = ">= 2.14.0"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

module "ops_stack" {
  source                 = "./ops-stack"
  metrics_server_enabled = var.metrics_server_enabled
  count                  = var.ops_stack_enabled ? 1 : 0
}
