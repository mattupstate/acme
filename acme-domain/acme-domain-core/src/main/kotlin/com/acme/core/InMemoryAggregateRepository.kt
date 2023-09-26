package com.acme.core

import java.time.Clock
import java.time.LocalDateTime

open class InMemoryAggregateRepository<T : Identifiable<I>, I>(
  objects: Set<T> = emptySet(),
  private val clock: Clock = Clock.systemUTC()
) :
  AggregateRepository<T, I> {

  private val objects: MutableMap<I, PersistedAggregate<T>> = mutableMapOf()

  init {
    objects.forEach(::save)
  }

  override fun find(id: I): PersistedAggregate<T>? = objects[id]

  override fun get(id: I): PersistedAggregate<T> =
    getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: I, block: () -> Throwable): PersistedAggregate<T> =
    find(id) ?: throw block()

  override fun exists(id: I): Boolean = objects.containsKey(id)

  override fun save(aggregate: T) {
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
