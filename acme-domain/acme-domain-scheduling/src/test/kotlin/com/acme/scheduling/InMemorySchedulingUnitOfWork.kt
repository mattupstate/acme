package com.acme.scheduling

import com.acme.core.AbstractUnitOfWork
import com.acme.core.Event
import com.acme.core.InMemoryAggregateRepository

class InMemorySchedulingUnitOfWork(
  appointments: InMemoryAggregateRepository<Appointment, Appointment.Id> = InMemoryAggregateRepository(),
  clients: InMemoryAggregateRepository<Client, Client.Id> = InMemoryAggregateRepository(),
  practitioners: InMemoryAggregateRepository<Practitioner, Practitioner.Id> = InMemoryAggregateRepository(),
  practices: InMemoryAggregateRepository<Practice, Practice.Id> = InMemoryAggregateRepository()
) : AbstractUnitOfWork(), SchedulingUnitOfWork {

  override val repositories = object : SchedulingPersistenceModule {
    override val appointments = appointments
    override val clients = clients
    override val practitioners = practitioners
    override val practices = practices
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
