package com.acme.web.server.scheduling.data

import com.acme.scheduling.Appointment
import com.acme.scheduling.AppointmentAttendedEvent
import com.acme.scheduling.AppointmentCanceledEvent
import com.acme.scheduling.AppointmentCreatedEvent
import com.acme.scheduling.AppointmentState
import com.acme.scheduling.AppointmentUnattendedEvent
import com.acme.scheduling.ClientCreatedEvent
import com.acme.scheduling.ContactPoint
import com.acme.scheduling.Period
import com.acme.scheduling.PracticeCreatedEvent
import com.acme.scheduling.PractitionerCreatedEvent
import com.acme.scheduling.data.SchedulingJooqUnitOfWork
import com.acme.sql.web_server.Tables.APPOINTMENTS
import com.acme.sql.web_server.Tables.CLIENTS
import com.acme.sql.web_server.Tables.CLIENT_CONTACT_POINTS
import com.acme.sql.web_server.Tables.CLIENT_NAMES
import com.acme.sql.web_server.Tables.PRACTICES
import com.acme.sql.web_server.Tables.PRACTICE_CONTACT_POINTS
import com.acme.sql.web_server.Tables.PRACTITIONERS
import com.acme.sql.web_server.Tables.PRACTITIONER_CONTACT_POINTS
import com.acme.sql.web_server.Tables.PRACTITIONER_NAMES
import java.time.LocalDateTime

val onPractitionerCreated: (PractitionerCreatedEvent, SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  with(uow.dsl) {
    val practitioner = event.practitioner

    insertInto(
      PRACTITIONERS,
      PRACTITIONERS.ID,
      PRACTITIONERS.GENDER
    ).values(
      practitioner.id.value,
      practitioner.gender.toString()
    ).execute()

    practitioner.names.map {
      val (start, end) = it.period.getTimeValues()

      insertInto(
        PRACTITIONER_NAMES,
        PRACTITIONER_NAMES.PRACTITIONER_ID,
        PRACTITIONER_NAMES.GIVEN,
        PRACTITIONER_NAMES.FAMILY,
        PRACTITIONER_NAMES.SUFFIX,
        PRACTITIONER_NAMES.PREFIX,
        PRACTITIONER_NAMES.PERIOD_START,
        PRACTITIONER_NAMES.PERIOD_END
      ).values(
        practitioner.id.value,
        it.given.value,
        it.family.value,
        it.suffix.value,
        it.prefix.value,
        start,
        end,
      )
    }.forEach(::execute)

    practitioner.contactPoints.map {
      insertInto(
        PRACTITIONER_CONTACT_POINTS,
        PRACTITIONER_CONTACT_POINTS.PRACTITIONER_ID,
        PRACTITIONER_CONTACT_POINTS.VALUE,
        PRACTITIONER_CONTACT_POINTS.SYSTEM,
        PRACTITIONER_CONTACT_POINTS.VERIFIED_AT,
      ).values(
        practitioner.id.value,
        it.value,
        it.toSystemString(),
        it.getVerifiedAtValue()
      )
    }.forEach(::execute)
  }
}

val onPracticeCreated: (PracticeCreatedEvent, SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  with(uow.dsl) {
    val practice = event.practice

    insertInto(
      PRACTICES,
      PRACTICES.ID,
      PRACTICES.NAME
    ).values(
      practice.id.value,
      practice.name.value
    ).execute()

    practice.contactPoints.map {
      insertInto(
        PRACTICE_CONTACT_POINTS,
        PRACTICE_CONTACT_POINTS.PRACTICE_ID,
        PRACTICE_CONTACT_POINTS.VALUE,
        PRACTICE_CONTACT_POINTS.SYSTEM,
        PRACTICE_CONTACT_POINTS.VERIFIED_AT,
      ).values(
        practice.id.value,
        it.value,
        it.toSystemString(),
        it.getVerifiedAtValue(),
      )
    }.forEach(::execute)
  }
}

val onClientCreated: (ClientCreatedEvent, uow: SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  with(uow.dsl) {
    val client = event.client

    insertInto(
      CLIENTS,
      CLIENTS.ID,
      CLIENTS.GENDER
    ).values(
      client.id.value,
      client.gender.toString()
    ).execute()

    client.names.map {
      val (start, end) = it.period.getTimeValues()

      insertInto(
        CLIENT_NAMES,
        CLIENT_NAMES.CLIENT_ID,
        CLIENT_NAMES.GIVEN,
        CLIENT_NAMES.FAMILY,
        CLIENT_NAMES.SUFFIX,
        CLIENT_NAMES.PREFIX,
        CLIENT_NAMES.PERIOD_START,
        CLIENT_NAMES.PERIOD_END
      ).values(
        client.id.value,
        it.given.value,
        it.family.value,
        it.suffix.value,
        it.prefix.value,
        start,
        end,
      )
    }.forEach(::execute)

    client.contactPoints.map {
      insertInto(
        CLIENT_CONTACT_POINTS,
        CLIENT_CONTACT_POINTS.CLIENT_ID,
        CLIENT_CONTACT_POINTS.VALUE,
        CLIENT_CONTACT_POINTS.SYSTEM,
        CLIENT_CONTACT_POINTS.VERIFIED_AT,
      ).values(
        client.id.value,
        it.value,
        it.toSystemString(),
        it.getVerifiedAtValue(),
      )
    }.forEach(::execute)
  }
}

val onAppointmentCreated: (AppointmentCreatedEvent, SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  val (start, end) = event.period.getTimeValues()

  uow.dsl.insertInto(
    APPOINTMENTS,
    APPOINTMENTS.ID,
    APPOINTMENTS.PRACTITIONER_ID,
    APPOINTMENTS.PRACTICE_ID,
    APPOINTMENTS.CLIENT_ID,
    APPOINTMENTS.STATE,
    APPOINTMENTS.PERIOD_START,
    APPOINTMENTS.PERIOD_END
  ).values(
    event.appointmentId.value,
    event.practitionerId.value,
    event.practiceId.value,
    event.clientId.value,
    event.state.toString(),
    start,
    end
  ).execute()
}

private fun updateAppointmentState(appointmentId: Appointment.Id, state: String, uow: SchedulingJooqUnitOfWork) {
  with(uow.dsl) {
    update(APPOINTMENTS).set(APPOINTMENTS.STATE, state)
      .where(APPOINTMENTS.ID.eq(appointmentId.value)).execute()
  }
}

val onAppointmentUnattended: (AppointmentUnattendedEvent, SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  updateAppointmentState(event.appointmentId, AppointmentState.UNATTENDED.toString(), uow)
}

val onAppointmentAttended: (AppointmentAttendedEvent, SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  updateAppointmentState(event.appointmentId, AppointmentState.ATTENDED.toString(), uow)
}

val onAppointmentCanceled: (AppointmentCanceledEvent, SchedulingJooqUnitOfWork) -> Unit = { event, uow ->
  updateAppointmentState(event.appointmentId, AppointmentState.CANCELED.toString(), uow)
}

fun Period.getTimeValues() = when (this) {
  is Period.Bounded -> Pair(start, end)
  is Period.Unbounded -> Pair(start, null)
  is Period.Unknown -> Pair(null, null)
}

fun ContactPoint.getVerifiedAtValue() = when (this) {
  is ContactPoint.Phone.Unverified -> null
  is ContactPoint.Phone.Verified -> LocalDateTime.now()
  is ContactPoint.Email.Unverified -> null
  is ContactPoint.Email.Verified -> LocalDateTime.now()
  is ContactPoint.SMS.Unverified -> null
  is ContactPoint.SMS.Verified -> LocalDateTime.now()
  is ContactPoint.Web -> null
}

fun ContactPoint.toSystemString() =
  when (this) {
    is ContactPoint.Phone -> "Phone"
    is ContactPoint.SMS -> "SMS"
    is ContactPoint.Email -> "Email"
    is ContactPoint.Web -> "Web"
  }
