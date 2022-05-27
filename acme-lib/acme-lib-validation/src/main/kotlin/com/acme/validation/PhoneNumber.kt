package com.acme.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [PhoneNumberValidator::class])
annotation class PhoneNumber(
  val message: String = "{com.acme.validation.PhoneNumber.message}",
  val region: String = "US",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)
