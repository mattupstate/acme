package com.acme.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class OneOfStringsValidator : ConstraintValidator<OneOfStrings, String> {

  private var annotation: OneOfStrings? = null

  override fun initialize(constraintAnnotation: OneOfStrings?) {
    annotation = constraintAnnotation
  }

  override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean =
    annotation!!.values.contains(value)
}
