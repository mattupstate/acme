package com.acme.ktor.server.metrics

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics
import io.micrometer.core.instrument.binder.jvm.JvmInfoMetrics
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.time.Duration
import javax.sql.DataSource

class PrometheusMetricsConfiguration {
  var registry: PrometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
  var path = "/metrics"
  var port = 8080
  var tags: List<Tag> = emptyList()
  var heapPressureLookback: Duration = Duration.ofMinutes(5)
  var heapPressureTestEvery: Duration = Duration.ofMinutes(1)
  var postgresDataSource: DataSource? = null
  var postgresDatabase: String? = null
}

val PrometheusMetrics = createApplicationPlugin(
  name = "PrometheusMetrics",
  createConfiguration = ::PrometheusMetricsConfiguration
) {
  val registry = pluginConfig.registry
  val path = pluginConfig.path
  val port = pluginConfig.port
  val tags = pluginConfig.tags
  val heapPressureLookback = pluginConfig.heapPressureLookback
  val heapPressureTestEvery = pluginConfig.heapPressureTestEvery
  val postgresDataSource = pluginConfig.postgresDataSource
  val postgresDatabase = pluginConfig.postgresDatabase

  val meterBinders = mutableListOf(
    ClassLoaderMetrics(tags),
    JvmMemoryMetrics(tags),
    JvmGcMetrics(tags),
    JvmThreadMetrics(tags),
    JvmCompilationMetrics(tags),
    JvmHeapPressureMetrics(tags, heapPressureLookback, heapPressureTestEvery),
    JvmInfoMetrics(),
    LogbackMetrics(tags),
  ).also {
    if (postgresDataSource != null && postgresDatabase != null) {
      it.add(PostgreSQLDatabaseMetrics(postgresDataSource, postgresDatabase))
    }
  }

  Metrics.addRegistry(registry)

  application.install(MicrometerMetrics) {
    this.registry = registry
    this.meterBinders = meterBinders
    timers { call, _ ->
      tag("path", call.request.path())
    }
  }

  application.routing {
    get(path) {
      if (call.request.local.port == port) {
        call.respondText(registry.scrape())
      } else {
        call.respond(HttpStatusCode.NotFound)
      }
    }
  }
}
