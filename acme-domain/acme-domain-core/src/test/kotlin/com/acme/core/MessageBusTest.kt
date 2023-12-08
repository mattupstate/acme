package com.acme.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MessageBusTest : ShouldSpec({

  class FakeException(message: String) : RuntimeException(message)

  data class FakeCommand(val value: String = "Hello") : Command {
    override val metadata: MetaData = MetaData()
  }

  data class FakeEvent(val value: String = "World") : Event {
    override val metadata: MetaData = MetaData()
  }

  class FakeUnitOfWork : UnitOfWork {
    private var _events: MutableList<Event> = mutableListOf()

    override val events: Sequence<Event>
      get() = sequence {
        while (_events.size > 0) yield(_events.removeAt(0))
      }

    override fun addEvent(event: Event) {
      _events.add(event)
    }

    override suspend fun <R> transaction(block: suspend () -> R): R = block()
  }

  should("handle commands and subsequent events") {
    val mb = DefaultMessageBus()
    var commandCalls = 0
    var eventCalls = 0

    suspend fun onCommand(cmd: FakeCommand, uow: FakeUnitOfWork) {
      commandCalls++
      uow.addEvent(FakeEvent("World"))
    }

    suspend fun onEvent(event: FakeEvent, uow: FakeUnitOfWork) {
      eventCalls++
    }

    val e = ::onEvent

    mb.addCommandHandler(FakeCommand::class to ::onCommand)
    mb.addEventHandler(FakeEvent::class, ::onEvent)

    mb.handle(FakeCommand("Hello"), FakeUnitOfWork())

    commandCalls.shouldBe(1)
    eventCalls.shouldBe(1)
  }

  should("throw exception when unable to handle command") {
    val mb = DefaultMessageBus()
    val exc = shouldThrow<RuntimeException> {
      mb.handle(FakeCommand(), FakeUnitOfWork())
    }
    exc.message.shouldBe("Unable to handle command of type ${FakeCommand::class.simpleName}")
  }

  should("not swallow command handler exceptions") {
    val mb = DefaultMessageBus()

    suspend fun onCommand(command: FakeCommand, uow: FakeUnitOfWork) {
      throw FakeException("command")
    }

    mb.addCommandHandler(FakeCommand::class to ::onCommand)

    val exc = shouldThrow<FakeException> {
      mb.handle(FakeCommand("Hello"), FakeUnitOfWork())
    }

    exc.message.shouldBe("command")
  }

  should("not swallow event handler exceptions") {
    val mb = DefaultMessageBus()

    suspend fun onCommand(command: FakeCommand, uow: FakeUnitOfWork) {
      uow.addEvent(FakeEvent("World"))
    }

    suspend fun onEvent(event: FakeEvent, uow: FakeUnitOfWork) {
      throw FakeException("event")
    }

    mb.addCommandHandler(FakeCommand::class to ::onCommand)
    mb.addEventHandler(FakeEvent::class to ::onEvent)

    val exc = shouldThrow<FakeException> {
      mb.handle(FakeCommand("Hello"), FakeUnitOfWork())
    }

    exc.message.shouldBe("event")
  }

  should("throw exception when repeated handlers are added") {
    val mb = DefaultMessageBus()

    suspend fun onCommand(command: FakeCommand, uow: FakeUnitOfWork) {
      uow.addEvent(FakeEvent("World"))
    }

    suspend fun onEvent(event: FakeEvent, uow: FakeUnitOfWork) {
      throw FakeException("event")
    }

    val exc = shouldThrow<RuntimeException> {
      mb.addCommandHandler(FakeCommand::class to ::onCommand, FakeCommand::class to ::onCommand)
    }
    exc.message.shouldBe("Command handler already exists")

    val exc2 = shouldThrow<RuntimeException> {
      mb.addEventHandler(FakeEvent::class to ::onEvent, FakeEvent::class to ::onEvent)
    }
    exc2.message.shouldBe("Event handler has already been registered")
  }
})
