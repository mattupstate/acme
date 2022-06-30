@file:OptIn(KtorExperimentalLocationsAPI::class)

package com.acme.web.server.scheduling.ktor

import com.acme.web.server.core.ktor.authenticatedUser
import com.acme.web.server.json.hal.HalLink
import com.acme.web.server.scheduling.data.SchedulingWebViews
import com.acme.web.server.scheduling.json.AppointmentResource
import com.acme.web.server.scheduling.json.ClientResource
import com.acme.web.server.scheduling.json.PracticeResource
import com.acme.web.server.scheduling.json.PractitionerResource
import com.acme.web.server.scheduling.toResource
import com.acme.web.server.security.AccessControlService
import com.acme.web.server.security.UnauthorizedAccessException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.get
import io.ktor.server.locations.locations
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext

fun Route.schedulingQueries(views: SchedulingWebViews, accessControl: AccessControlService, basePath: String = "") {
  fun PipelineContext<Unit, ApplicationCall>.href(location: Any): String =
    basePath + locations.href(location)

  get<RootResourceLocation> {
    call.respond(HttpStatusCode.NoContent)
  }

  get<PracticeResourceLocation> { location ->
    val href = href(PracticeResourceLocation(location.id))

    if (!accessControl.canReadResource(href, call.authenticatedUser().id)) {
      throw UnauthorizedAccessException()
    }

    val practice = views.findPracticeOrThrow(location.id)

    call.respond(
      practice.toResource {
        PracticeResource.Links(
          self = HalLink(href)
        )
      }
    )
  }

  get<PractitionerResourceLocation> { location ->
    val href = href(PractitionerResourceLocation(location.id))

    if (!accessControl.canReadResource(href, call.authenticatedUser().id)) {
      throw UnauthorizedAccessException()
    }

    val practitioner = views.findPractitionerOrThrow(location.id)

    call.respond(
      practitioner.toResource {
        PractitionerResource.Links(
          self = HalLink(href)
        )
      }
    )
  }

  get<ClientResourceLocation> { location ->
    val href = href(ClientResourceLocation(location.id))

    if (!accessControl.canReadResource(href, call.authenticatedUser().id)) {
      throw UnauthorizedAccessException()
    }

    val client = views.findClientOrThrow(location.id)

    call.respond(
      client.toResource {
        ClientResource.Links(
          self = HalLink(href)
        )
      }
    )
  }

  get<AppointmentResourceLocation> { location ->
    val href = href(location)

    if (!accessControl.canReadResource(href, call.authenticatedUser().id)) {
      throw UnauthorizedAccessException()
    }

    val appointment = views.findAppointmentOrThrow(location.id)

    call.respond(
      appointment.toResource {
        AppointmentResource.Links(
          self = HalLink(href),
          client = HalLink(
            href(ClientResourceLocation(appointment.clientId))
          ),
          practitioner = HalLink(
            href(ClientResourceLocation(appointment.practitionerId))
          ),
          practice = HalLink(
            href(PracticeResourceLocation(appointment.practiceId))
          ),
          cancel = HalLink(
            href(AppointmentResourceLocation.Cancel(location))
          ),
          markAttended = HalLink(
            href(AppointmentResourceLocation.Attended(location))
          ),
          markUnattended = HalLink(
            href(AppointmentResourceLocation.Unattended(location))
          ),
        )
      }
    )
  }
}
