package com.acme.web.api.scheduling

import com.acme.web.api.toJsonPointer
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator

val constraintValidator: Validator = Validation.buildDefaultValidatorFactory().validator

fun withJsonPointerViolations(obj: Any, block: (Map<String, Set<String>>) -> Unit) {
  block(
    constraintValidator.validate(obj).groupBy {
      it.propertyPath.toJsonPointer()
    }.map {
      it.key to it.value.map(ConstraintViolation<*>::getMessage).toSet()
    }.toMap()
  )
}
