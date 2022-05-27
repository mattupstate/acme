package com.acme.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class AppointmentTest : ShouldSpec({

  val fixture = Appointment(
    id = Appointment.Id("Appointment123"),
    practitioner = Practitioner.Id("Practitioner123"),
    client = Client.Id("Client123"),
    practice = Practice.Id("Practice123"),
    period = Period.Bounded(LocalDateTime.now(), LocalDateTime.now().plusSeconds(100)),
    state = AppointmentState.SCHEDULED
  )

  should("be equal when having the same ID") {
    val fixture2 = fixture.copy()
    fixture2.shouldBe(fixture)

    val fixture3 = fixture.copy(id = Appointment.Id("Blah"))
    fixture3.shouldNotBe(fixture)
  }

  should("have an initial revision of 1") {
    val appointment = Appointment(
      id = Appointment.Id("Appointment123"),
      practitioner = Practitioner.Id("Practitioner123"),
      client = Client.Id("Client123"),
      practice = Practice.Id("Practice123"),
      period = Period.Bounded(LocalDateTime.MIN, LocalDateTime.MAX),
      state = AppointmentState.SCHEDULED
    )

    appointment.revision.shouldBe(1)
  }

  should("markAttended updates state and revision") {
    val appointment = fixture.markAttended()
    appointment.state.shouldBe(AppointmentState.ATTENDED)
    appointment.revision.shouldBe(2)
  }

  should("markUnatended updates state and revision") {
    val appointment = fixture.markUnattended()
    appointment.state.shouldBe(AppointmentState.UNATTENDED)
    appointment.revision.shouldBe(2)
  }

  should("cancel updates state and revision") {
    val appointment = fixture.cancel()
    appointment.state.shouldBe(AppointmentState.CANCELED)
    appointment.revision.shouldBe(2)
  }
})
