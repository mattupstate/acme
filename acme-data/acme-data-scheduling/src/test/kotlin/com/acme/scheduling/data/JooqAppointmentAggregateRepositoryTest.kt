package com.acme.scheduling.data

import com.acme.scheduling.Appointment
import com.acme.scheduling.Appointment.Id
import com.acme.scheduling.AppointmentState
import com.acme.scheduling.Client
import com.acme.scheduling.Period
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import com.acme.test.IntegrationTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class JooqAppointmentAggregateRepositoryTest : ShouldSpec({
  tags(IntegrationTest)

  val jooq = listener(JooqAndPostgresListener())
  val now = LocalDateTime.now()

  val appointment = Appointment(
    id = Appointment.Id("Appointment123"),
    period = Period.Bounded(
      start = now,
      end = now.plusHours(1)
    ),
    client = Client.Id("Client123"),
    practitioner = Practitioner.Id("Practitioner123"),
    practice = Practice.Id("Practice123"),
    state = AppointmentState.SCHEDULED
  )

  should("save new aggregate") {
    jooq.testTransaction {
      val repo = JooqAppointmentAggregateRepository(it.dsl())
      repo.save(appointment)
      repo.get(appointment.id).shouldBe(appointment)
      repo.exists(appointment.id).shouldBeTrue()
    }
  }

  should("update existing aggregate") {
    jooq.testTransaction {
      val repo = JooqAppointmentAggregateRepository(it.dsl())
      repo.save(appointment)
      val updatedAppointment = appointment.copy(
        revision = 2,
        period = Period.Bounded(
          start = now.plusDays(1),
          end = now.plusDays(1).plusHours(1)
        )
      )
      repo.save(updatedAppointment)
      repo.get(appointment.id).shouldBe(updatedAppointment)
    }
  }

  should("throw NoSuchElementException") {
    jooq.testTransaction {
      val repo = JooqAppointmentAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.get(appointment.id)
      }
    }
  }

  should("throw user supplied exception") {
    jooq.testTransaction {
      val repo = JooqAppointmentAggregateRepository(it.dsl())
      shouldThrow<FakeException> {
        repo.getOrThrow(appointment.id) {
          FakeException()
        }
      }
    }
  }
})
