package com.acme.web.api.scheduling

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

fun CreateAppointmentCommandRequest.toCommand(id: String, authenticatedUser: AcmeWebUserPrincipal) =
  CreateAppointmentCommand(
    id = Appointment.Id(id),
    practitioner = Practitioner.Id(practitionerId!!),
    client = Client.Id(clientId!!),
    practice = Practice.Id(practiceId!!),
    state = AppointmentState.valueOf(state!!),
    period = com.acme.scheduling.Period.Bounded(from!!, to!!),
  ).apply {
    metadata.set("principal" to authenticatedUser)
  }

fun CreateClientCommandRequest.toCommand(id: String, authenticatedUser: AcmeWebUserPrincipal) =
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
  ).apply {
    metadata.set("principal" to authenticatedUser)
  }

fun CreatePracticeCommandRequest.toCommand(id: String, authenticatedUser: AcmeWebUserPrincipal) =
  CreatePracticeCommand(
    id = Practice.Id(id),
    owner = Practitioner.Id(authenticatedUser.id),
    name = Practice.Name(name!!),
    contactPoints = phoneNumbers!!.map {
      ContactPoint.Phone.Unverified(it)
    }.plus(
      emailAddresses!!.map {
        ContactPoint.Email.Unverified(it)
      }
    ).toSet()
  ).apply {
    metadata.set("principal" to authenticatedUser)
  }

fun CreatePractitionerCommandRequest.toCommand(id: String, authenticatedUser: AcmeWebUserPrincipal) =
  CreatePractitionerCommand(
    id = Practitioner.Id(id),
    user = UserId(authenticatedUser.id),
    name = name!!.toValueObject(),
    gender = Gender.valueOf(gender!!),
    contactPoints = phoneNumbers!!.map {
      ContactPoint.Phone.Unverified(it)
    }.plus(
      emailAddresses!!.map {
        ContactPoint.Email.Unverified(it)
      }
    ).toSet()
  ).apply {
    metadata.set("principal" to authenticatedUser)
  }
