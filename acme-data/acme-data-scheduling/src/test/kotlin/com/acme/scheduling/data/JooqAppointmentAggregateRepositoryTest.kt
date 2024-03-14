package com.acme.scheduling.data

import com.acme.scheduling.Appointment
import com.acme.scheduling.AppointmentState
import com.acme.scheduling.Client
import com.acme.scheduling.Period
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class JooqAppointmentAggregateRepositoryTest : ShouldSpec({

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
    testTransaction {
      val time = timeFixtureFactory()
      val repo = JooqAppointmentAggregateRepository(it.dsl(), time.clock)
      repo.save(appointment)
      repo.exists(appointment.id).shouldBeTrue()

      val persistedAppointment = repo.findById(appointment.id).getOrThrow()
      persistedAppointment.aggregate.shouldBe(appointment)
      with(persistedAppointment.metaData) {
        revision.shouldBe(1)
        createdAt.shouldBe(time.now)
        updatedAt.shouldBe(time.now)
      }
    }
  }

  should("update existing aggregate and increment revision") {
    testTransaction {
      val createTime = timeFixtureFactory()
      val createRepo = JooqAppointmentAggregateRepository(it.dsl(), createTime.clock)
      createRepo.save(appointment)

      val updateTime = timeFixtureFactory()
      val updateRepo = JooqAppointmentAggregateRepository(it.dsl(), updateTime.clock)
      val expectedAppointment = appointment.copy(
        period = Period.Bounded(
          start = now.plusDays(1),
          end = now.plusDays(1).plusHours(1)
        )
      )
      updateRepo.save(expectedAppointment)

      val persistedAppointment = updateRepo.findById(appointment.id).getOrThrow()
      persistedAppointment.aggregate.shouldBe(expectedAppointment)
      with(persistedAppointment.metaData) {
        revision.shouldBe(2)
        createdAt.shouldBe(createTime.now)
        updatedAt.shouldBe(updateTime.now)
      }
    }
  }

  should("throw NoSuchElementException") {
    testTransaction {
      val repo = JooqAppointmentAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.findById(appointment.id).getOrThrow()
      }
    }
  }
})
