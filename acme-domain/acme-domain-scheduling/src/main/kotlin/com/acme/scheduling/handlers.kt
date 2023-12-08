package com.acme.scheduling

import com.acme.core.CommandValidationException
import com.acme.core.DefaultMessageBus

suspend fun createPractice(command: CreatePracticeCommand, uow: SchedulingUnitOfWork) {
  Practice(
    id = command.id,
    owner = command.owner,
    name = command.name,
    contactPoints = command.contactPoints
  )
    .also {
      uow.repositories.practices.save(it)
    }
    .also {
      uow.addEvent(PracticeCreatedEvent(it))
    }
}

suspend fun createClient(command: CreateClientCommand, uow: SchedulingUnitOfWork) {
  Client(
    id = command.id,
    names = setOf(command.name),
    gender = command.gender,
    contactPoints = command.contactPoints
  ).also { uow.repositories.clients.save(it) }
    .also {
      uow.addEvent(ClientCreatedEvent(it))
    }
}

suspend fun createPractitioner(command: CreatePractitionerCommand, uow: SchedulingUnitOfWork) {
  Practitioner(
    id = command.id,
    user = command.user,
    gender = command.gender,
    names = setOf(command.name),
    contactPoints = command.contactPoints
  ).also { uow.repositories.practitioners.save(it) }
    .also {
      uow.addEvent(PractitionerCreatedEvent(it))
    }
}

suspend fun createAppointment(command: CreateAppointmentCommand, uow: SchedulingUnitOfWork) {
  val errors = mutableSetOf<CommandValidationException.CommandValidationError>()
  if (!uow.repositories.practices.exists(command.practice)) {
    errors.add(
      CommandValidationException.InvalidAggregateReferenceError(
        CreateAppointmentCommand::practice,
        command.practice.value,
        "Invalid practice"
      )
    )
  }

  if (!uow.repositories.practitioners.exists(command.practitioner)) {
    errors.add(
      CommandValidationException.InvalidAggregateReferenceError(
        CreateAppointmentCommand::practitioner,
        command.practitioner.value,
        "Invalid practitioner"
      )
    )
  }

  if (!uow.repositories.clients.exists(command.client)) {
    errors.add(
      CommandValidationException.InvalidAggregateReferenceError(
        CreateAppointmentCommand::client,
        command.client.value,
        "Invalid client"
      )
    )
  }

  if (errors.size > 0) {
    throw CommandValidationException(command, errors)
  }

  Appointment(
    id = command.id,
    client = command.client,
    practitioner = command.practitioner,
    practice = command.practice,
    state = command.state,
    period = command.period,
  )
    .also { uow.repositories.appointments.save(it) }
    .also {
      uow.addEvent(
        AppointmentCreatedEvent(
          appointmentId = it.id,
          clientId = it.client,
          practitionerId = it.practitioner,
          practiceId = it.practice,
          period = it.period,
          state = it.state,
        )
      )
    }
}

suspend fun markAppointmentAttended(command: MarkAppointmentAttendedCommand, uow: SchedulingUnitOfWork) {
  uow.repositories.appointments.getOrThrow(command.appointment) {
    CommandValidationException(
      command,
      CommandValidationException.InvalidAggregateReferenceError(
        MarkAppointmentAttendedCommand::appointment,
        command.appointment.value,
        "Invalid appointment"
      )
    )
  }.aggregate
    .markAttended()
    .also { uow.repositories.appointments.save(it) }
    .also {
      uow.addEvent(AppointmentAttendedEvent(it.id))
    }
}

suspend fun markAppointmentUnattended(command: MarkAppointmentUnattendedCommand, uow: SchedulingUnitOfWork) {
  uow.repositories.appointments.getOrThrow(command.appointment) {
    CommandValidationException(
      command,
      CommandValidationException.InvalidAggregateReferenceError(
        MarkAppointmentAttendedCommand::appointment,
        command.appointment.value,
        "Invalid appointment"
      )
    )
  }.aggregate.markUnattended()
    .also { uow.repositories.appointments.save(it) }
    .also {
      uow.addEvent(AppointmentUnattendedEvent(it.id))
    }
}

suspend fun cancelAppointment(command: CancelAppointmentCommand, uow: SchedulingUnitOfWork) {
  uow.repositories.appointments.getOrThrow(command.appointment) {
    CommandValidationException(
      command,
      CommandValidationException.InvalidAggregateReferenceError(
        MarkAppointmentAttendedCommand::appointment,
        command.appointment.value,
        "Invalid appointment"
      )
    )
  }.aggregate.cancel()
    .also { uow.repositories.appointments.save(it) }
    .also {
      uow.addEvent(AppointmentCanceledEvent(it.id))
    }
}

val schedulingMessageBus = DefaultMessageBus()
  .addCommandHandler(
    CreatePracticeCommand::class to ::createPractice,
    CreatePractitionerCommand::class to ::createPractitioner,
    CreateClientCommand::class to ::createClient,
    CreateAppointmentCommand::class to ::createAppointment,
    MarkAppointmentAttendedCommand::class to ::markAppointmentAttended,
    MarkAppointmentUnattendedCommand::class to ::markAppointmentUnattended,
    CancelAppointmentCommand::class to ::cancelAppointment,
  )
