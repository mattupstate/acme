package com.acme.web.api.scheduling

import com.acme.core.Command
import com.acme.scheduling.Appointment
import com.acme.scheduling.AppointmentState
import com.acme.scheduling.Client
import com.acme.scheduling.ContactPoint
import com.acme.scheduling.CreateAppointmentCommand
import com.acme.scheduling.CreateClientCommand
import com.acme.scheduling.CreatePracticeCommand
import com.acme.scheduling.CreatePractitionerCommand
import com.acme.scheduling.Gender
import com.acme.scheduling.Name
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import com.acme.scheduling.UserId
import com.acme.web.api.security.AcmeWebUserPrincipal
import java.time.LocalDateTime

fun HumanName.toValueObject() =
  com.acme.scheduling.HumanName(
    family = Name.Family(family!!),
    given = Name.Given(given!!),
    prefix = Name.Prefix(prefix!!),
    suffix = Name.Suffix(suffix!!),
    period = (period ?: Period()).toValueObject()
  )

fun Period.toValueObject() =
  if (start != null && end != null) {
    com.acme.scheduling.Period.Bounded(LocalDateTime.from(start), LocalDateTime.from(end))
  } else if (start != null) {
    com.acme.scheduling.Period.Unbounded(LocalDateTime.from(start))
  } else {
    com.acme.scheduling.Period.Unknown
  }

data class WebContext(
  val authenticatedUser: AcmeWebUserPrincipal
)

fun CreateAppointmentCommandRequest.toCommand(id: String, ctx: WebContext) =
  CreateAppointmentCommand(
    id = Appointment.Id(id),
    practitioner = Practitioner.Id(practitionerId!!),
    client = Client.Id(clientId!!),
    practice = Practice.Id(practiceId!!),
    state = AppointmentState.valueOf(state!!),
    period = com.acme.scheduling.Period.Bounded(from!!, to!!),
  ).applyWebMetaData(ctx)

fun CreateClientCommandRequest.toCommand(id: String, ctx: WebContext) =
  CreateClientCommand(
    id = Client.Id(id),
    name = name!!.toValueObject(),
    gender = Gender.valueOf(gender!!),
    contactPoints = phoneNumbers!!.map {
      ContactPoint.Phone.Unverified(it)
    }.plus(
      emailAddresses!!.map {
        ContactPoint.Email.Unverified(it)
      }
    ).toSet()
  ).applyWebMetaData(ctx)

fun CreatePracticeCommandRequest.toCommand(id: String, ctx: WebContext) =
  CreatePracticeCommand(
    id = Practice.Id(id),
    owner = Practitioner.Id(ctx.authenticatedUser.id),
    name = Practice.Name(name!!),
    contactPoints = phoneNumbers!!.map {
      ContactPoint.Phone.Unverified(it)
    }.plus(
      emailAddresses!!.map {
        ContactPoint.Email.Unverified(it)
      }
    ).toSet()
  ).applyWebMetaData(ctx)

fun CreatePractitionerCommandRequest.toCommand(id: String, ctx: WebContext) =
  CreatePractitionerCommand(
    id = Practitioner.Id(id),
    user = UserId(ctx.authenticatedUser.id),
    name = name!!.toValueObject(),
    gender = Gender.valueOf(gender!!),
    contactPoints = phoneNumbers!!.map {
      ContactPoint.Phone.Unverified(it)
    }.plus(
      emailAddresses!!.map {
        ContactPoint.Email.Unverified(it)
      }
    ).toSet()
  ).applyWebMetaData(ctx)

fun <C : Command> C.applyWebMetaData(ctx: WebContext): C {
  metadata.set("principal" to ctx.authenticatedUser)
  return this
}
