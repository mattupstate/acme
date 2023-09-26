package com.acme.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class InMemoryAggregateRepositoryTest : ShouldSpec({

  data class FakeAggregate(
    override val id: String,
  ) : Identifiable<String>

  class FakeException : RuntimeException()

  should("contain aggregates provided through constructor") {
    val aggregate = FakeAggregate("id123")
    val repo = InMemoryAggregateRepository(setOf(aggregate))
    repo.exists(aggregate.id).shouldBeTrue()
    val persistedAggregate = repo.get(aggregate.id)
    persistedAggregate.aggregate.shouldBe(aggregate)
  }

  should("should save new aggregate") {
    val repo = InMemoryAggregateRepository<FakeAggregate, String>()
    val aggregate = FakeAggregate("id123")
    repo.save(aggregate)
    repo.exists(aggregate.id).shouldBeTrue()
    val persistedAggregate = repo.get(aggregate.id)
    persistedAggregate.aggregate.shouldBe(aggregate)
  }

  should("throw user supplied exception") {
    val repo = InMemoryAggregateRepository<FakeAggregate, String>()
    shouldThrow<FakeException> {
      repo.getOrThrow("id123") { FakeException() }
    }
  }
})
