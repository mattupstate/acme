package com.acme.core

interface AggregateRepository<T : Identifiable<I>, I> {
  suspend fun find(id: I): PersistedAggregate<T>?
  suspend fun get(id: I): PersistedAggregate<T>
  suspend fun getOrThrow(id: I, block: () -> Throwable): PersistedAggregate<T>
  suspend fun exists(id: I): Boolean
  suspend fun save(aggregate: T)
}
