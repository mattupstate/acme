package com.acme.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class AbstractUnitOfWorkTest : ShouldSpec({

  class FakeUnitOfWork : AbstractUnitOfWork() {
    override suspend fun <R> transaction(block: suspend () -> R): R = throw NotImplementedError()
  }

  data class FakeEvent(val value: String) : Event {
    override val metadata: MetaData = MetaData()
  }

  should("throw NotImplementedError") {
    val uow = FakeUnitOfWork()
    shouldThrow<NotImplementedError> {
      uow.transaction {}
    }
  }

  should("add events") {
    val uow = FakeUnitOfWork()
    val event = FakeEvent("Hello")
    uow.addEvent(event)
    uow.events.toList() shouldBe listOf(event)
  }
})
