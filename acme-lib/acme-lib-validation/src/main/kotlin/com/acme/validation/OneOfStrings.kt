package com.acme.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [OneOfStringsValidator::class])
annotation class OneOfStrings(
  val values: Array<String> = [],
  val message: String = "{com.acme.validation.OneOfStrings.message}",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)
