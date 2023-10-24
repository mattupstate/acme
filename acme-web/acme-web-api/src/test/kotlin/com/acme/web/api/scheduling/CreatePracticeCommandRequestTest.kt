package com.acme.web.api.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class CreatePracticeCommandRequestTest : ShouldSpec({

  val fixture = CreatePracticeCommandRequest(
    name = "Hello Practice",
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

  should("fail with null fields") {
    withJsonPointerViolations(
      fixture.copy(
        name = null,
        phoneNumbers = null,
        emailAddresses = null
      )
    ) {
      it.shouldBe(
        mapOf(
          "/name" to setOf("must not be blank"),
          "/phoneNumbers" to setOf("must not be null"),
          "/emailAddresses" to setOf("must not be null"),
        )
      )
    }
  }

  should("fail with empty fields") {
    withJsonPointerViolations(
      fixture.copy(
        name = "",
        phoneNumbers = emptyList(),
        emailAddresses = emptyList()
      )
    ) {
      it.shouldBe(
        mapOf(
          "/name" to setOf("must not be blank"),
          "/phoneNumbers" to setOf("size must be between 1 and 100"),
          "/emailAddresses" to setOf("size must be between 1 and 100"),
        )
      )
    }
  }

  should("fail with invalid phone numbers and malformed email addresses") {
    withJsonPointerViolations(
      fixture.copy(
        phoneNumbers = listOf(
          "555-555-5555"
        ),
        emailAddresses = listOf(
          "hello"
        )
      )
    ) {
      it.shouldBe(
        mapOf(
          "/phoneNumbers/0" to setOf("must be a valid phone number"),
          "/emailAddresses/0" to setOf("must be a well-formed email address"),
        )
      )
    }
  }
})
