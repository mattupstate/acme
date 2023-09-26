package com.acme.core

interface AggregateRepository<T : Identifiable<I>, I> {
  fun find(id: I): PersistedAggregate<T>?
  fun get(id: I): PersistedAggregate<T>
  fun getOrThrow(id: I, block: () -> Throwable): PersistedAggregate<T>
  fun exists(id: I): Boolean
  fun save(aggregate: T)
}
