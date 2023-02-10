resource "vault_database_secret_backend_connection" "keto" {
  backend = vault_mount.database.path
  name    = "keto"

  allowed_roles = [
    "keto-dba",
    "keto-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@keto-postgresql.default.svc.cluster.local:5432/keto"
  }

  depends_on = [
    helm_release.keto_postgresql
  ]
}

resource "vault_database_secret_backend_role" "keto_dba" {
  name                = "keto-dba"
  backend             = vault_mount.database.path
  db_name             = vault_database_secret_backend_connection.keto.name
  # NOTE: dba credentials last for 1 hour
  default_ttl         = 60 * 60
  max_ttl             = 60 * 60
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT keto_dba TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE keto_dba;"
  ]
}

resource "vault_database_secret_backend_role" "keto_app" {
  name                = "keto-app"
  backend             = vault_mount.database.path
  db_name             = vault_database_secret_backend_connection.keto.name
  # NOTE: app credentials last for 18 days
  default_ttl         = 60 * 60 * 24 * 18
  max_ttl             = 60 * 60 * 24 * 18
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT keto_app TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE keto_app;"
  ]
}

#----

resource "vault_database_secret_backend_connection" "kratos" {
  backend = vault_mount.database.path
  name    = "kratos"

  allowed_roles = [
    "kratos-dba",
    "kratos-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@kratos-postgresql.default.svc.cluster.local:5432/kratos"
  }

  depends_on = [
    helm_release.kratos_postgresql
  ]
}

resource "vault_database_secret_backend_role" "kratos_dba" {
  name                = "kratos-dba"
  backend             = vault_mount.database.path
  db_name             = vault_database_secret_backend_connection.kratos.name
  # NOTE: dba credentials last for 1 hour
  default_ttl         = 60 * 60
  max_ttl             = 60 * 60
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT kratos_dba TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE kratos_dba;"
  ]
}

resource "vault_database_secret_backend_role" "kratos_app" {
  name                = "kratos-app"
  backend             = vault_mount.database.path
  db_name             = vault_database_secret_backend_connection.kratos.name
  # NOTE: app credentials last for 18 days
  default_ttl         = 60 * 60 * 24 * 18
  max_ttl             = 60 * 60 * 24 * 18
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT kratos_app TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE kratos_app;"
  ]
}

#----

resource "vault_database_secret_backend_connection" "acme" {
  backend = vault_mount.database.path
  name    = "acme-web-server"

  allowed_roles = [
    "acme-data-sql-dba",
    "acme-data-sql-app",
  ]

  postgresql {
    username       = "vault"
    password       = "rotate-me-immediately"
    connection_url = "postgresql://{{username}}:{{password}}@acme-postgresql.default.svc.cluster.local:5432/acme"
  }

  depends_on = [
    helm_release.acme_postgresql
  ]
}

resource "vault_database_secret_backend_role" "acme_data_sql_dba" {
  name                = "acme-data-sql-dba"
  backend             = vault_mount.database.path
  db_name             = vault_database_secret_backend_connection.acme.name
  # NOTE: dba credentials last for 1 hour
  default_ttl         = 60 * 60
  max_ttl             = 60 * 60
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT acme_data_sql_dba TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE acme_data_sql_dba;"
  ]
}

resource "vault_database_secret_backend_role" "acme_data_sql_app" {
  name                = "acme-data-sql-app"
  backend             = vault_mount.database.path
  db_name             = vault_database_secret_backend_connection.acme.name
  # NOTE: app credentials last for 18 days
  default_ttl         = 60 * 60 * 24 * 18
  max_ttl             = 60 * 60 * 24 * 18
  creation_statements = [
    "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' INHERIT VALID UNTIL '{{expiration}}';",
    "GRANT acme_data_sql_app TO \"{{name}}\"",
    "ALTER ROLE \"{{name}}\" SET ROLE acme_data_sql_app;"
  ]
}
