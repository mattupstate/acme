package com.acme.scheduling

import com.acme.core.AbstractUnitOfWork
import com.acme.core.Event
import com.acme.core.InMemoryAggregateRepository

class InMemorySchedulingUnitOfWork(
  appointments: Set<Appointment> = emptySet(),
  clients: Set<Client> = emptySet(),
  practitioners: Set<Practitioner> = emptySet(),
  practices: Set<Practice> = emptySet(),
) : AbstractUnitOfWork(), SchedulingUnitOfWork {

  override val repositories = object : SchedulingPersistenceModule {
    override val appointments = InMemoryAggregateRepository(appointments)
    override val clients = InMemoryAggregateRepository(clients)
    override val practitioners = InMemoryAggregateRepository(practitioners)
    override val practices = InMemoryAggregateRepository(practices)
  }

  private var _events: MutableList<Event> = mutableListOf()

  override val events: Sequence<Event>
    get() = sequence {
      while (_events.size > 0) yield(_events.removeAt(0))
    }

  override fun addEvent(event: Event) {
    _events.add(event)
  }

  override suspend fun <R> transaction(block: suspend () -> R): R =
    block()
}
