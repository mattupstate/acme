@file:OptIn(KtorExperimentalLocationsAPI::class)

package com.acme.web.server.scheduling.ktor

import com.acme.app.web.server.core.ktor.KetoConfiguration
import com.acme.app.web.server.scheduling.data.JooqSchedulingWebViews
import com.acme.app.web.server.scheduling.schedulingMessageBus
import com.acme.app.web.server.security.AccessControlService
import com.acme.app.web.server.security.keto.ktor.KtorOryKetoClient
import com.acme.app.web.server.security.keto.ktor.defaultOryKetoClientConfiguration
import com.acme.app.web.server.security.ktor.KtorOryKetoAccessControlService
import com.acme.scheduling.data.SchedulingJooqUnitOfWork
import io.ktor.client.HttpClient
import io.ktor.server.auth.authenticate
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.routing.Route
import org.jooq.Configuration

fun Route.scheduling(jooqConfig: Configuration, ketoConfig: KetoConfiguration, basePath: String = "") {
  val accessControl = KtorOryKetoAccessControlService(
    "scheduling",
    KtorOryKetoClient(
      HttpClient {
        defaultOryKetoClientConfiguration(ketoConfig.readBaseUrl)
      },
      HttpClient {
        defaultOryKetoClientConfiguration(ketoConfig.writeBaseUrl)
      }
    )
  )
  scheduling(jooqConfig, accessControl, basePath)
}

fun Route.scheduling(jooqConfig: Configuration, accessControl: AccessControlService, basePath: String = "") {
  val schedulingUnitOfWork = SchedulingJooqUnitOfWork(jooqConfig)
  val schedulingWebViews = JooqSchedulingWebViews(jooqConfig)

  authenticate {
    schedulingCommands(schedulingMessageBus, accessControl, schedulingUnitOfWork, basePath)
    schedulingQueries(schedulingWebViews, accessControl, basePath)
  }
}
