package com.acme.validation

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import jakarta.validation.Validation
import jakarta.validation.ValidatorFactory

class OneOfStringsTest: ShouldSpec({
  data class FakeBean(
    @field:OneOfStrings(values = "Doctor, Nurse, Pediatrician")
    val title: String?
  )

  should("result in errors for invalid value") {
    val validator = Validation.buildDefaultValidatorFactory().validator
    val results = validator.validate(FakeBean("Doc"))
    results shouldHaveSize 1
  }
})
