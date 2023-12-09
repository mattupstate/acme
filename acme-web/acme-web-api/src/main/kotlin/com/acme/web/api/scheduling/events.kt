package com.acme.web.api.scheduling

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
import com.acme.sql.web.tables.references.APPOINTMENTS
import com.acme.sql.web.tables.references.CLIENTS
import com.acme.sql.web.tables.references.CLIENT_CONTACT_POINTS
import com.acme.sql.web.tables.references.CLIENT_NAMES
import com.acme.sql.web.tables.references.PRACTICES
import com.acme.sql.web.tables.references.PRACTICE_CONTACT_POINTS
import com.acme.sql.web.tables.references.PRACTITIONERS
import com.acme.sql.web.tables.references.PRACTITIONER_CONTACT_POINTS
import com.acme.sql.web.tables.references.PRACTITIONER_NAMES
import kotlinx.coroutines.reactive.awaitFirst
import java.time.LocalDateTime

suspend fun onPractitionerCreated(event: PractitionerCreatedEvent, uow: SchedulingJooqUnitOfWork) {
  with(uow.dsl) {
    val practitioner = event.practitioner

    val insertResult = insertInto(
      PRACTITIONERS,
      PRACTITIONERS.ID,
      PRACTITIONERS.GENDER
    ).values(
      practitioner.id.value,
      practitioner.gender.toString()
    ).returningResult(PRACTITIONERS.ID)
      .awaitFirst()

    val batchQueries = practitioner.names.map {
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
        insertResult[PRACTITIONERS.ID],
        it.given.value,
        it.family.value,
        it.suffix.value,
        it.prefix.value,
        start,
        end,
      )
    } + practitioner.contactPoints.map {
      insertInto(
        PRACTITIONER_CONTACT_POINTS,
        PRACTITIONER_CONTACT_POINTS.PRACTITIONER_ID,
        PRACTITIONER_CONTACT_POINTS.VALUE,
        PRACTITIONER_CONTACT_POINTS.SYSTEM,
        PRACTITIONER_CONTACT_POINTS.VERIFIED_AT,
      ).values(
        insertResult[PRACTITIONERS.ID],
        it.value,
        it.toSystemString(),
        it.getVerifiedAtValue()
      )
    }

    batch(batchQueries).awaitFirst()
  }
}

suspend fun onPracticeCreated(event: PracticeCreatedEvent, uow: SchedulingJooqUnitOfWork) {
  with(uow.dsl) {
    val practice = event.practice

    val insertResult = insertInto(
      PRACTICES,
      PRACTICES.ID,
      PRACTICES.NAME
    ).values(
      practice.id.value,
      practice.name.value
    ).returning(PRACTICES.ID)
      .awaitFirst()

    val batchQueries = practice.contactPoints.map {
      insertInto(
        PRACTICE_CONTACT_POINTS,
        PRACTICE_CONTACT_POINTS.PRACTICE_ID,
        PRACTICE_CONTACT_POINTS.VALUE,
        PRACTICE_CONTACT_POINTS.SYSTEM,
        PRACTICE_CONTACT_POINTS.VERIFIED_AT,
      ).values(
        insertResult[PRACTICES.ID],
        it.value,
        it.toSystemString(),
        it.getVerifiedAtValue(),
      )
    }

    batch(batchQueries).awaitFirst()
  }
}

suspend fun onClientCreated(event: ClientCreatedEvent, uow: SchedulingJooqUnitOfWork) {
  with(uow.dsl) {
    val client = event.client

    val insertResult = insertInto(
      CLIENTS,
      CLIENTS.ID,
      CLIENTS.GENDER
    ).values(
      client.id.value,
      client.gender.toString()
    ).returning(CLIENTS.ID).awaitFirst()

    val batchQueries = client.names.map {
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
        insertResult[CLIENTS.ID],
        it.given.value,
        it.family.value,
        it.suffix.value,
        it.prefix.value,
        start,
        end,
      )
    } + client.contactPoints.map {
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
    }

    batch(batchQueries).awaitFirst()
  }
}

suspend fun onAppointmentCreated(event: AppointmentCreatedEvent, uow: SchedulingJooqUnitOfWork) {
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
  ).returning().awaitFirst()
}

private suspend fun updateAppointmentState(
  appointmentId: Appointment.Id,
  state: String,
  uow: SchedulingJooqUnitOfWork
) {
  with(uow.dsl) {
    update(APPOINTMENTS).set(APPOINTMENTS.STATE, state)
      .where(APPOINTMENTS.ID.eq(appointmentId.value)).returning().awaitFirst()
  }
}

suspend fun onAppointmentUnattended(event: AppointmentUnattendedEvent, uow: SchedulingJooqUnitOfWork) {
  updateAppointmentState(event.appointmentId, AppointmentState.UNATTENDED.toString(), uow)
}

suspend fun onAppointmentAttended(event: AppointmentAttendedEvent, uow: SchedulingJooqUnitOfWork) {
  updateAppointmentState(event.appointmentId, AppointmentState.ATTENDED.toString(), uow)
}

suspend fun onAppointmentCanceled(event: AppointmentCanceledEvent, uow: SchedulingJooqUnitOfWork) {
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
