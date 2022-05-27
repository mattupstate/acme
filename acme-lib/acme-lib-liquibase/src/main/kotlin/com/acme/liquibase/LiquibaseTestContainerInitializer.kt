package com.acme.liquibase

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import liquibase.resource.CompositeResourceAccessor
import liquibase.resource.FileSystemResourceAccessor
import java.io.File
import java.sql.Connection

fun update(baseDir: String, changelogFile: String, connection: Connection) {
  Liquibase(
    changelogFile,
    CompositeResourceAccessor(
      ClassLoaderResourceAccessor(),
      FileSystemResourceAccessor(File(baseDir))
    ),
    JdbcConnection(connection)
  ).update(Contexts())
}

fun update(connection: Connection) {
  val baseDir = System.getProperty("liquibase.baseDir") ?: System.getProperty("user.dir")
  val changelogFile = System.getProperty("liquibase.changelogFile") ?: "db/changelog.yaml"
  update(baseDir, changelogFile, connection)
}
