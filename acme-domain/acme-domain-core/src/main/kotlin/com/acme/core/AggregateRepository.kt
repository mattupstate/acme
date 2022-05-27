package com.acme.core

interface AggregateRepository<T : Identifiable<I>, I> {
  fun find(id: I): T?
  fun get(id: I): T
  fun getOrThrow(id: I, block: () -> Throwable): T
  fun exists(id: I): Boolean
  fun save(aggregate: T)
}
