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
  config_path    = "~/.kube/config"
  config_context = var.kubectx
}

provider "helm" {
  kubernetes {
    config_path    = "~/.kube/config"
    config_context = var.kubectx
  }
}
