package com.acme.scheduling

import com.acme.core.CommandValidationException
import com.acme.core.DefaultMessageBus
import com.acme.core.InvalidAggregateReferenceError

suspend fun createPractice(command: CreatePracticeCommand, uow: SchedulingUnitOfWork) {
  Practice(
    id = command.id,
    owner = command.owner,
    name = command.name,
    contactPoints = command.contactPoints
  ).also {
    uow.repositories.practices.save(it)
    uow.addEvent(PracticeCreatedEvent(it))
  }
}

suspend fun createClient(command: CreateClientCommand, uow: SchedulingUnitOfWork) {
  Client(
    id = command.id,
    names = setOf(command.name),
    gender = command.gender,
    contactPoints = command.contactPoints
  ).also {
    uow.repositories.clients.save(it)
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
  ).also {
    uow.repositories.practitioners.save(it)
    uow.addEvent(PractitionerCreatedEvent(it))
  }
}

suspend fun createAppointment(command: CreateAppointmentCommand, uow: SchedulingUnitOfWork) {
  listOf(
    uow.repositories.practices.exists(command.practice) to {
      InvalidAggregateReferenceError(
        CreateAppointmentCommand::practice,
        command.practice.value,
        "Invalid practice"
      )
    },
    uow.repositories.practitioners.exists(command.practitioner) to {
      InvalidAggregateReferenceError(
        CreateAppointmentCommand::practitioner,
        command.practitioner.value,
        "Invalid practitioner"
      )
    },
    uow.repositories.clients.exists(command.client) to {
       InvalidAggregateReferenceError(
         CreateAppointmentCommand::client,
         command.client.value,
         "Invalid client"
       )
     }
  )
    .filter { !it.first }
    .map { it.second() }
    .toSet()
    .also {
      if (it.isNotEmpty()) {
        throw CommandValidationException(command, it)
      }
  }

  Appointment(
    id = command.id,
    client = command.client,
    practitioner = command.practitioner,
    practice = command.practice,
    state = command.state,
    period = command.period,
  ).also {
    uow.repositories.appointments.save(it)
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
  uow.repositories.appointments.findById(command.appointment)
    .onSuccess {
      it.aggregate.markAttended().also {
        uow.repositories.appointments.save(it)
        uow.addEvent(AppointmentAttendedEvent(it.id))
      }
    }
    .onFailure {
      when (it) {
        is NoSuchElementException -> throw CommandValidationException(
          command,
          InvalidAggregateReferenceError(
            MarkAppointmentAttendedCommand::appointment,
            command.appointment.value,
            "Invalid appointment"
          )
        )
        else -> throw it
      }
    }
}

suspend fun markAppointmentUnattended(command: MarkAppointmentUnattendedCommand, uow: SchedulingUnitOfWork) {
  uow.repositories.appointments.findById(command.appointment)
    .onSuccess {
      it.aggregate.markUnattended().also {
        uow.repositories.appointments.save(it)
        uow.addEvent(AppointmentUnattendedEvent(it.id))
      }
    }.onFailure {
      when (it) {
        is NoSuchElementException -> throw CommandValidationException(
          command,
          InvalidAggregateReferenceError(
            MarkAppointmentAttendedCommand::appointment,
            command.appointment.value,
            "Invalid appointment"
          )
        )
        else -> throw it
      }
    }
}

suspend fun cancelAppointment(command: CancelAppointmentCommand, uow: SchedulingUnitOfWork) {
  uow.repositories.appointments.findById(command.appointment)
    .onSuccess {
      it.aggregate.cancel().also {
        uow.repositories.appointments.save(it)
        uow.addEvent(AppointmentCanceledEvent(it.id))
      }
    }
    .onFailure {
      when (it) {
        is NoSuchElementException -> throw CommandValidationException(
          command,
          InvalidAggregateReferenceError(
            MarkAppointmentAttendedCommand::appointment,
            command.appointment.value,
            "Invalid appointment"
          )
        )
        else -> throw it
      }

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
