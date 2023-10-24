@file:OptIn(KtorExperimentalLocationsAPI::class)

package com.acme.web.api.scheduling.ktor

import com.acme.scheduling.data.SchedulingJooqUnitOfWork
import com.acme.web.api.core.ktor.KetoConfiguration
import com.acme.web.api.scheduling.data.JooqSchedulingWebViews
import com.acme.web.api.scheduling.schedulingMessageBus
import com.acme.web.api.security.AccessControlService
import com.acme.web.api.security.keto.ktor.KtorOryKetoClient
import com.acme.web.api.security.keto.ktor.defaultOryKetoClientConfiguration
import com.acme.web.api.security.ktor.KtorOryKetoAccessControlService
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
        defaultOryKetoClientConfiguration(ketoConfig.readUrl)
      },
      HttpClient {
        defaultOryKetoClientConfiguration(ketoConfig.writeUrl)
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
