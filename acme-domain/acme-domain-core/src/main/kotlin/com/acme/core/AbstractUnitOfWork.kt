package com.acme.core

abstract class AbstractUnitOfWork : UnitOfWork {
  private var _events: MutableList<Event> = mutableListOf()

  override val events: Sequence<Event>
    get() = sequence {
      while (_events.size > 0) yield(_events.removeAt(0))
    }

  override fun addEvent(event: Event) {
    _events.add(event)
  }

  abstract override suspend fun <R> transaction(block: suspend () -> R): R
}
