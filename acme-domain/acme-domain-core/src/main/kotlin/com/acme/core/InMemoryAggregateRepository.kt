package com.acme.core

open class InMemoryAggregateRepository<T : Identifiable<I>, I>(objects: Set<T> = emptySet()) :
  AggregateRepository<T, I> {

  private val objects: MutableMap<I, T> = objects.associateBy { it.id }.toMutableMap()

  override fun find(id: I): T? = objects[id]

  override fun get(id: I): T =
    getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: I, block: () -> Throwable): T =
    find(id) ?: throw block()

  override fun exists(id: I): Boolean = objects.containsKey(id)

  override fun save(aggregate: T) {
    objects[aggregate.id] = aggregate
  }
}
