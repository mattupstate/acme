package com.acme.web.api.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class CreateAppointmentRequestCommandTest : ShouldSpec({

  val fixture = CreateAppointmentCommandRequest(
    clientId = "client123",
    practitionerId = "practitioner123",
    practiceId = "practice123",
    state = "SCHEDULED",
    from = LocalDateTime.now(),
    to = LocalDateTime.now().plusHours(1),
  )

  should("pass validation") {
    withJsonPointerViolations(fixture) {
      it.shouldBe(emptyMap())
    }
  }

  should("fail validation with null values") {
    withJsonPointerViolations(
      fixture.copy(clientId = null, practitionerId = null, practiceId = null, from = null, to = null)
    ) {
      it.shouldBe(
        mapOf(
          "/clientId" to setOf("must not be null"),
          "/practitionerId" to setOf("must not be null"),
          "/practiceId" to setOf("must not be null"),
          "/from" to setOf("must not be null"),
          "/to" to setOf("must not be null"),
        )
      )
    }
  }
})
