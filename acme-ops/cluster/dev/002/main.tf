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

provider "vault" {
  address = "https://vault-127-0-0-1.nip.io"
  token   = var.vault_root_token
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
