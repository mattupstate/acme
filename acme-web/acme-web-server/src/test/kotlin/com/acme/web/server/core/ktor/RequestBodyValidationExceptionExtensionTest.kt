package com.acme.web.server.core.ktor

import com.acme.ktor.server.validation.RequestBodyValidationException
import com.acme.web.server.json.hal.VndError
import com.acme.web.server.scheduling.constraintValidator
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class RequestBodyValidationExceptionExtensionTest : ShouldSpec({

  data class FakeBean(
    @field:NotNull
    @field:Size(min = 1, max = 100)
    val myField: List<String>? = null,

    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val myOtherField: String? = null,
  )

  context("toVndError") {
    should("return one error for each field and prefer NotNull and NotBlank over other annotations") {
      RequestBodyValidationException(
        constraintValidator.validate(FakeBean())
      ).toVndError() shouldBe (
        VndError(
          message = "Validation error",
          total = 2,
          embedded = VndError.Embedded(
            listOf(
              VndError(
                message = "must not be null",
                path = "/myField"
              ),
              VndError(
                message = "must not be blank",
                path = "/myOtherField"
              )
            )
          )
        )
      )
    }
  }
})
