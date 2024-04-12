package com.acme.web.api.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class CreatePractitionerCommandRequestTest : ShouldSpec({

  val fixture = CreatePractitionerCommandRequest(
    name = HumanName(
      family = "Hello",
      given = "world",
      period = Period(Instant.MIN, Instant.MAX)
    ),
    gender = "UNKNOWN",
    phoneNumbers = listOf(
      "917-555-5555"
    ),
    emailAddresses = listOf(
      "hello@world.com"
    )
  )

  should("pass validation") {
    withJsonPointerViolations(fixture) {
      it.shouldBe(emptyMap())
    }
  }

  should("fail validation with null values") {
    withJsonPointerViolations(
      fixture.copy(name = null, gender = null, phoneNumbers = null, emailAddresses = null)
    ) {
      it.shouldBe(
        mapOf(
          "/name" to setOf("must not be null"),
          "/gender" to setOf("must not be null"),
          "/gender" to setOf(
            "must not be null",
            "must be one of MALE, FEMALE, TRANSGENDER, NON_BINARY, OTHER, UNKNOWN"
          ),
          "/phoneNumbers" to setOf("must not be null"),
          "/emailAddresses" to setOf("must not be null"),
        )
      )
    }
  }

  should("fail validation with empty values") {
    withJsonPointerViolations(fixture.copy(phoneNumbers = emptyList(), emailAddresses = emptyList())) {
      it.shouldBe(
        mapOf(
          "/phoneNumbers" to setOf("size must be between 1 and 100"),
          "/emailAddresses" to setOf("size must be between 1 and 100"),
        )
      )
    }
  }

  should("fail validation with blank values") {
    withJsonPointerViolations(fixture.copy(phoneNumbers = listOf(""), emailAddresses = listOf(""))) {
      it.shouldBe(
        mapOf(
          "/phoneNumbers/0" to setOf(
            "must not be blank",
            "must be a valid phone number"
          ),
          "/emailAddresses/0" to setOf(
            "must not be blank"
          )
        )
      )
    }
  }
})
