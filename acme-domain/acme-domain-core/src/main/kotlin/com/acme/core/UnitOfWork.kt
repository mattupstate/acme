package com.acme.core

interface UnitOfWork {
  val events: Sequence<Event>
  fun addEvent(event: Event)
  suspend fun <R> transaction(block: suspend () -> R): R
}
