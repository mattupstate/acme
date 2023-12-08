package com.acme.web.api.scheduling

import com.acme.web.api.core.toJsonPointer
import io.kotest.core.Tag
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator

object SchedulingTest : Tag()

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
