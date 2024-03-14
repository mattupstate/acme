package com.acme.core

import java.time.Clock
import java.time.LocalDateTime

open class InMemoryAggregateRepository<T : Identifiable<I>, I>(
  private val clock: Clock = Clock.systemUTC()
) : AggregateRepository<T, I> {

  private val objects: MutableMap<I, PersistedAggregate<T>> = mutableMapOf()

  override suspend fun findById(id: I): Result<PersistedAggregate<T>> = runCatching {
    objects.get(id) ?: throw NoSuchElementException()
  }

  override suspend fun exists(id: I): Boolean = objects.containsKey(id)

  override suspend fun save(aggregate: T) {
    val now = LocalDateTime.now(clock)

    objects[aggregate.id] = objects[aggregate.id]?.let {
      PersistedAggregate(
        aggregate = aggregate,
        metaData = it.metaData.copy(
          revision = it.metaData.revision + 1,
          updatedAt = now
        )
      )
    } ?: run {
      PersistedAggregate(
        aggregate = aggregate,
        metaData = PersistenceMetaData(
          revision = 1,
          createdAt = now,
          updatedAt = now,
        )
      )
    }
  }
}
