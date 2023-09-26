package com.acme.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
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

  should("markAttended updates state and revision") {
    val appointment = fixture.markAttended()
    appointment.state.shouldBe(AppointmentState.ATTENDED)
  }

  should("markUnattended updates state and revision") {
    val appointment = fixture.markUnattended()
    appointment.state.shouldBe(AppointmentState.UNATTENDED)
  }

  should("cancel updates state and revision") {
    val appointment = fixture.cancel()
    appointment.state.shouldBe(AppointmentState.CANCELED)
  }
})
