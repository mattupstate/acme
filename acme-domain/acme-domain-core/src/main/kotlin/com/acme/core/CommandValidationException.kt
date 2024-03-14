package com.acme.core

import kotlin.reflect.KProperty

class CommandValidationException(
  val command: Any,
  val errors: Set<CommandValidationError>
) : IllegalArgumentException("Invalid command") {

  constructor(command: Any, error: CommandValidationError) : this(command, setOf(error))
}

interface CommandValidationError {
  val message: String
}

data class InvalidAggregateReferenceError(
  val fieldName: String,
  val value: String,
  override val message: String,
) : CommandValidationError {
  constructor(property: KProperty<Any>, value: String, message: String) : this(property.name, value, message)
}
