package com.acme.scheduling

import com.acme.core.CommandValidationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class CommandHandlerTest : ShouldSpec({

  context("createPractice") {
    should("save aggregate and publish event") {
      val cmd = CreatePracticeCommand(
        id = Practice.Id("Practice123"),
        owner = Practitioner.Id("Practitioner123"),
        name = Practice.Name("Somebody & Associates"),
        contactPoints = emptySet()
      )

      val uow = InMemorySchedulingUnitOfWork()

      createPractice(cmd, uow)

      uow.repositories.practices.exists(cmd.id).shouldBeTrue()

      uow.events.toList().shouldBe(
        listOf(
          PracticeCreatedEvent(
            Practice(
              id = Practice.Id("Practice123"),
              owner = Practitioner.Id("Practitioner123"),
              name = Practice.Name("Somebody & Associates"),
              contactPoints = emptySet()
            )
          )
        )
      )
    }
  }

  context("createPractitioner") {
    should("save aggregate and publish event") {
      val cmd = CreatePractitionerCommand(
        id = Practitioner.Id("Practitioner123"),
        user = UserId("User123"),
        name = HumanName(
          family = Name.Family("World"),
          given = Name.Given("Hello"),
          prefix = Name.Prefix(""),
          suffix = Name.Suffix(""),
          period = Period.Unknown
        ),
        gender = Gender.UNKNOWN,
        contactPoints = setOf(
          ContactPoint.Phone.Unverified("917-555-5555")
        )
      )

      val uow = InMemorySchedulingUnitOfWork()

      createPractitioner(cmd, uow)

      uow.repositories.practitioners.exists(cmd.id).shouldBeTrue()

      uow.events.toList().shouldBe(
        listOf(
          PractitionerCreatedEvent(
            Practitioner(
              id = Practitioner.Id("Practitioner123"),
              user = UserId("User123"),
              names = setOf(
                HumanName(
                  given = Name.Given("Hello"),
                  family = Name.Family("World"),
                  suffix = Name.Suffix(""),
                  prefix = Name.Prefix(""),
                  period = Period.Unknown
                )
              ),
              gender = Gender.UNKNOWN,
              contactPoints = setOf(
                ContactPoint.Phone.Unverified("917-555-5555")
              )
            )
          )
        )
      )
    }
  }

  context("createClient") {
    should("save aggregate and publish event") {
      val cmd = CreateClientCommand(
        id = Client.Id("Client123"),
        name = HumanName(
          family = Name.Family("World"),
          given = Name.Given("Hello"),
          prefix = Name.Prefix(""),
          suffix = Name.Suffix(""),
          period = Period.Unknown
        ),
        gender = Gender.UNKNOWN,
        contactPoints = setOf(
          ContactPoint.Phone.Unverified("917-555-5555")
        )
      )

      val uow = InMemorySchedulingUnitOfWork()

      createClient(cmd, uow)

      uow.repositories.clients.exists(cmd.id).shouldBeTrue()

      uow.events.toList().shouldBe(
        listOf(
          ClientCreatedEvent(
            Client(
              id = Client.Id("Client123"),
              names = setOf(
                HumanName(
                  given = Name.Given("Hello"),
                  family = Name.Family("World"),
                  suffix = Name.Suffix(""),
                  prefix = Name.Prefix(""),
                  period = Period.Unknown
                )
              ),
              gender = Gender.UNKNOWN,
              contactPoints = setOf(
                ContactPoint.Phone.Unverified("917-555-5555")
              )
            )
          )
        )
      )
    }
  }

  context("createAppointment") {
    should("save aggregate and publish event") {
      val practice = Practice(
        id = Practice.Id("Practice123"),
        name = Practice.Name("Hello & Associates"),
        owner = Practitioner.Id("Practitioner123"),
        contactPoints = emptySet()
      )

      val practitioner = Practitioner(
        id = Practitioner.Id("Practitioner123"),
        user = UserId("User123"),
        names = setOf(
          HumanName(
            given = Name.Given("First"),
            family = Name.Family("Last"),
            suffix = Name.Suffix(""),
            prefix = Name.Prefix(""),
            period = Period.Unknown
          )
        ),
        gender = Gender.UNKNOWN,
        contactPoints = emptySet()
      )

      val client = Client(
        id = Client.Id("Client123"),
        names = setOf(
          HumanName(
            given = Name.Given("First"),
            family = Name.Family("Last"),
            suffix = Name.Suffix(""),
            prefix = Name.Prefix(""),
            period = Period.Unknown
          )
        ),
        gender = Gender.UNKNOWN
      )

      val cmd = CreateAppointmentCommand(
        id = Appointment.Id("Appointment123"),
        practice = practice.id,
        practitioner = practitioner.id,
        client = client.id,
        state = AppointmentState.SCHEDULED,
        period = Period.Bounded(LocalDateTime.MIN, LocalDateTime.MAX),
      )

      val uow = InMemorySchedulingUnitOfWork().apply {
        with(repositories) {
          practices.save(practice)
          practitioners.save(practitioner)
          clients.save(client)
        }
      }

      createAppointment(cmd, uow)

      uow.repositories.appointments.exists(cmd.id).shouldBeTrue()

      uow.events.toList().shouldBe(
        listOf(
          AppointmentCreatedEvent(
            appointmentId = Appointment.Id("Appointment123"),
            practiceId = Practice.Id("Practice123"),
            clientId = Client.Id("Client123"),
            practitionerId = Practitioner.Id("Practitioner123"),
            period = Period.Bounded(LocalDateTime.MIN, LocalDateTime.MAX),
            state = AppointmentState.SCHEDULED,
          )
        )
      )
    }
  }

  context("markAppointmentAttended") {
    should("should update aggregate and publish event") {
      val appointment = Appointment(
        id = Appointment.Id("Appointment123"),
        practice = Practice.Id("Practice123"),
        client = Client.Id("Client123"),
        practitioner = Practitioner.Id("Practitioner123"),
        period = Period.Bounded(LocalDateTime.MIN, LocalDateTime.MAX),
        state = AppointmentState.SCHEDULED
      )

      val cmd = MarkAppointmentAttendedCommand(Appointment.Id("Appointment123"))
      val uow = InMemorySchedulingUnitOfWork()
      uow.repositories.appointments.save(appointment)

      markAppointmentAttended(cmd, uow)

      uow.events.toList().shouldBe(
        listOf(AppointmentAttendedEvent(appointment.id))
      )
    }

    should("markAppointmentAttended should throw exception when aggregate does not exist") {
      val cmd = MarkAppointmentAttendedCommand(Appointment.Id("Appointment123"))
      val uow = InMemorySchedulingUnitOfWork()

      val exc = shouldThrow<CommandValidationException> {
        markAppointmentAttended(cmd, uow)
      }

      exc.command.shouldBe(cmd)
      exc.errors.shouldBe(
        setOf(
          CommandValidationException.InvalidAggregateReferenceError(
            MarkAppointmentAttendedCommand::appointment,
            cmd.appointment.value,
            "Invalid appointment"
          )
        )
      )
    }
  }

  context("markAppointmentUnattended") {
    should("update aggregate and publish event") {
      val appointment = Appointment(
        id = Appointment.Id("Appointment123"),
        practice = Practice.Id("Practice123"),
        client = Client.Id("Client123"),
        practitioner = Practitioner.Id("Practitioner123"),
        period = Period.Bounded(LocalDateTime.MIN, LocalDateTime.MAX),
        state = AppointmentState.SCHEDULED
      )

      val cmd = MarkAppointmentUnattendedCommand(Appointment.Id("Appointment123"))
      val uow = InMemorySchedulingUnitOfWork()
      uow.repositories.appointments.save(appointment)

      markAppointmentUnattended(cmd, uow)

      uow.events.toList().shouldBe(
        listOf(AppointmentUnattendedEvent(appointment.id))
      )
    }

    should("throw exception when aggregate does not exist") {
      val cmd = MarkAppointmentUnattendedCommand(Appointment.Id("Appointment123"))
      val uow = InMemorySchedulingUnitOfWork()

      val exc = shouldThrow<CommandValidationException> {
        markAppointmentUnattended(cmd, uow)
      }

      exc.command.shouldBe(cmd)
      exc.errors.shouldBe(
        setOf(
          CommandValidationException.InvalidAggregateReferenceError(
            MarkAppointmentAttendedCommand::appointment,
            cmd.appointment.value,
            "Invalid appointment"
          )
        )
      )
    }
  }

  context("cancelAppointment") {
    should("update aggregate and publish event") {
      val appointment = Appointment(
        id = Appointment.Id("Appointment123"),
        practice = Practice.Id("Practice123"),
        client = Client.Id("Client123"),
        practitioner = Practitioner.Id("Practitioner123"),
        period = Period.Bounded(LocalDateTime.MIN, LocalDateTime.MAX),
        state = AppointmentState.SCHEDULED
      )

      val cmd = CancelAppointmentCommand(Appointment.Id("Appointment123"))
      val uow = InMemorySchedulingUnitOfWork()
      uow.repositories.appointments.save(appointment)

      cancelAppointment(cmd, uow)

      uow.events.toList().shouldBe(
        listOf(AppointmentCanceledEvent(appointment.id))
      )
    }
  }

  should("throw exception when aggregate does not exist") {
    val cmd = CancelAppointmentCommand(Appointment.Id("Appointment123"))
    val uow = InMemorySchedulingUnitOfWork()

    val exc = shouldThrow<CommandValidationException> {
      cancelAppointment(cmd, uow)
    }

    exc.command.shouldBe(cmd)
    exc.errors.shouldBe(
      setOf(
        CommandValidationException.InvalidAggregateReferenceError(
          MarkAppointmentAttendedCommand::appointment,
          cmd.appointment.value,
          "Invalid appointment"
        )
      )
    )
  }
})
