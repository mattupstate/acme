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
    val repo = InMemoryAggregateRepository<FakeAggregate, String>().apply { save(aggregate) }
    repo.exists(aggregate.id).shouldBeTrue()
    repo.findById(aggregate.id).getOrThrow().aggregate.shouldBe(aggregate)
  }

  should("should save new aggregate") {
    val repo = InMemoryAggregateRepository<FakeAggregate, String>()
    val aggregate = FakeAggregate("id123")
    repo.save(aggregate)
    repo.exists(aggregate.id).shouldBeTrue()
    repo.findById(aggregate.id).getOrThrow().aggregate.shouldBe(aggregate)
  }

  should("result in NoSuchElementException") {
    val repo = InMemoryAggregateRepository<FakeAggregate, String>()
    shouldThrow<NoSuchElementException> {
      repo.findById("id123").getOrThrow()
    }
  }
})
