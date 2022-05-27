package com.acme.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class InMemoryAggregateRepositoryTest : ShouldSpec({

  data class FakeAggregate(
    override val id: String,
    override val revision: Int,
  ) : Identifiable<String>, HasRevision

  class FakeException : RuntimeException()

  should("contain aggregates provided through constructor") {
    val aggregate = FakeAggregate("id123", 1)
    val repo = InMemoryAggregateRepository(setOf(aggregate))
    aggregate.shouldBe(repo.get(aggregate.id))
    repo.exists(aggregate.id).shouldBeTrue()
  }

  should("should save new aggregate") {
    val repo = InMemoryAggregateRepository<FakeAggregate, String>()
    val aggregate = FakeAggregate("id123", 1)
    repo.save(aggregate)

    aggregate.shouldBe(repo.get(aggregate.id))
    repo.exists(aggregate.id).shouldBeTrue()
  }

  should("throw user supplied exception") {
    val repo = InMemoryAggregateRepository<FakeAggregate, String>()
    shouldThrow<FakeException> {
      repo.getOrThrow("id123") { FakeException() }
    }
  }
})
