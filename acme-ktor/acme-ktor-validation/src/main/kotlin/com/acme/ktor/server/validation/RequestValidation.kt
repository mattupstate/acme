package com.acme.ktor.server.validation

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.request.receive
import io.ktor.util.AttributeKey
import jakarta.validation.ConstraintViolation
import jakarta.validation.Path
import jakarta.validation.Validation
import jakarta.validation.Validator
import kotlinx.serialization.SerializationException

fun RequestDecodingException.getUnderlyingCause(): String =
  when (val cause = this.cause) {
    is RequestDecodingException -> cause.getUnderlyingCause()
    else -> cause!!.message!!.let {
      it.substring(0, it.indexOf("JSON input") - 1)
    }
  }

class RequestBodyValidationException(
  val constraintViolations: Map<Path, List<ConstraintViolation<*>>>
) : RuntimeException("Validation error") {
  constructor(constraintViolations: Set<ConstraintViolation<*>>) : this(
    constraintViolations.groupBy(ConstraintViolation<*>::getPropertyPath)
  )
}

class RequestDecodingException(message: String, cause: Throwable) : RuntimeException(message, cause)

suspend inline fun <reified T : Any> ApplicationCall.receiveAndValidate(): T =
  try {
    receive<T>().also(::validate)
  } catch (exc: SerializationException) {
    throw RequestDecodingException("JSON processing error", exc)
  }

fun ApplicationCall.validate(obj: Any): Set<ConstraintViolation<Any>> =
  attributes[requestValidationKey].validate(obj).also {
    if (it.isNotEmpty()) throw RequestBodyValidationException(it)
  }

class RequestValidationConfiguration {
  var validator: Validator = Validation.buildDefaultValidatorFactory().validator
}

val requestValidationKey = AttributeKey<Validator>("requestValidationKey")

val RequestValidation = createApplicationPlugin(
  name = "RequestValidation",
  createConfiguration = ::RequestValidationConfiguration
) {
  onCall { call ->
    call.attributes.put(requestValidationKey, pluginConfig.validator)
  }
}
