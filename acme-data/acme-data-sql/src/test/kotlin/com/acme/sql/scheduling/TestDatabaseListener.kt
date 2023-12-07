package com.acme.sql.scheduling

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import org.jooq.SQLDialect
import org.jooq.impl.DSL

class TestDatabaseListener : TestListener {

  val dsl = DSL.using(
    ConnectionFactories.get(
      ConnectionFactoryOptions
        .parse("r2dbc:tc:postgresql:///test?TC_IMAGE_TAG=15.5")
        .mutate()
        .option(ConnectionFactoryOptions.USER, "test")
        .option(ConnectionFactoryOptions.PASSWORD, "test")
        .build()
    ), SQLDialect.POSTGRES
  )

  override suspend fun beforeSpec(spec: Spec) {
  }

  override suspend fun afterSpec(spec: Spec) {
  }
}
