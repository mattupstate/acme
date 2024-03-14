package com.acme.web.api.scheduling

import com.acme.core.MessageBus
import com.acme.ktor.server.validation.receiveAndValidate
import com.acme.scheduling.Appointment
import com.acme.scheduling.CancelAppointmentCommand
import com.acme.scheduling.MarkAppointmentAttendedCommand
import com.acme.scheduling.MarkAppointmentUnattendedCommand
import com.acme.scheduling.SchedulingUnitOfWork
import com.acme.web.api.authenticatedUser
import com.acme.web.api.defaultIdGenerator
import com.acme.web.api.scheduling.json.AppointmentCollectionResource
import com.acme.web.api.scheduling.json.ClientCollectionResource
import com.acme.web.api.scheduling.json.PracticeCollectionResource
import com.acme.web.api.scheduling.json.PractitionerCollectionResource
import com.acme.web.api.security.AccessControlService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.locations
import io.ktor.server.locations.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext

@KtorExperimentalLocationsAPI
fun Route.schedulingCommands(
  messageBus: MessageBus,
  accessControl: AccessControlService,
  unitOfWork: SchedulingUnitOfWork,
  basePath: String = ""
) {
  fun PipelineContext<Unit, ApplicationCall>.href(location: Any): String =
    basePath + locations.href(location)

  post<PracticeCollectionResourceLocation> {
    val principal = call.authenticatedUser()
    val command = call.receiveAndValidate<CreatePracticeCommandRequest>()
      .toCommand(defaultIdGenerator(), WebContext(principal))
    val resourceHref = href(PracticeResourceLocation(command.id.value))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
      accessControl.setResourceOwner(resourceHref, principal.id)
    }

    call.respond(
      PracticeCollectionResource.withLinks(
        self = href(PracticeCollectionResourceLocation()),
        items = listOf(resourceHref)
      )
    )
  }

  post<ClientCollectionResourceLocation> {
    val principal = call.authenticatedUser()
    val command = call.receiveAndValidate<CreateClientCommandRequest>()
      .toCommand(defaultIdGenerator(), WebContext(principal))
    val resourceHref = href(ClientResourceLocation(command.id.value))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
      accessControl.setResourceOwner(resourceHref, principal.id)
    }

    call.respond(
      ClientCollectionResource.withLinks(
        self = href(ClientCollectionResourceLocation()),
        items = listOf(resourceHref)
      )
    )
  }

  post<PractitionerCollectionResourceLocation> {
    val principal = call.authenticatedUser()
    val command = call.receiveAndValidate<CreatePractitionerCommandRequest>()
      .toCommand(defaultIdGenerator(), WebContext(principal))
    val resourceHref = href(PractitionerResourceLocation(command.id.value))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
      accessControl.setResourceOwner(resourceHref, principal.id)
    }

    call.respond(
      PractitionerCollectionResource.withLinks(
        self = href(PractitionerCollectionResourceLocation()),
        items = listOf(resourceHref)
      )
    )
  }

  post<AppointmentCollectionResourceLocation> {
    val principal = call.authenticatedUser()
    val command = call.receiveAndValidate<CreateAppointmentCommandRequest>()
      .toCommand(defaultIdGenerator(), WebContext(principal))
    val resourceHref = href(AppointmentResourceLocation(command.id.value))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
      accessControl.setResourceOwner(resourceHref, principal.id)
    }

    call.respond(
      AppointmentCollectionResource.withLinks(
        self = href(AppointmentCollectionResourceLocation()),
        items = listOf()
      )
    )
  }

  post<AppointmentResourceLocation.Attended> { location ->
    val command = MarkAppointmentAttendedCommand(Appointment.Id(location.parent.id))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
    }

    call.respond(HttpStatusCode.NoContent)
  }

  post<AppointmentResourceLocation.Unattended> { location ->
    val command = MarkAppointmentUnattendedCommand(Appointment.Id(location.parent.id))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
    }

    call.respond(HttpStatusCode.NoContent)
  }

  post<AppointmentResourceLocation.Cancel> { location ->
    val command = CancelAppointmentCommand(Appointment.Id(location.parent.id))

    unitOfWork.transaction {
      messageBus.handle(command, unitOfWork)
    }

    call.respond(HttpStatusCode.NoContent)
  }
}
