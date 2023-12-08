package com.acme.scheduling

import com.acme.core.AbstractUnitOfWork
import com.acme.core.Event
import com.acme.core.InMemoryAggregateRepository

class InMemorySchedulingUnitOfWork : AbstractUnitOfWork(), SchedulingUnitOfWork {

  override val repositories = object : SchedulingPersistenceModule {
    override val appointments = InMemoryAggregateRepository<Appointment, Appointment.Id>()
    override val clients = InMemoryAggregateRepository<Client, Client.Id>()
    override val practitioners = InMemoryAggregateRepository<Practitioner, Practitioner.Id>()
    override val practices = InMemoryAggregateRepository<Practice, Practice.Id>()
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
