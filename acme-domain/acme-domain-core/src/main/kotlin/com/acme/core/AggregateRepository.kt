package com.acme.core

interface AggregateRepository<T : Identifiable<I>, I> {
  suspend fun findById(id: I): Result<PersistedAggregate<T>>
  suspend fun exists(id: I): Boolean
  suspend fun save(aggregate: T)
}
