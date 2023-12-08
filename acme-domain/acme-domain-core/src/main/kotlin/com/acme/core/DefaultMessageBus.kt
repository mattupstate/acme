package com.acme.core

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlin.reflect.KSuspendFunction2

class DefaultMessageBus(
  private val commandHandlers: MutableMap<KClass<*>, KSuspendFunction2<Command, UnitOfWork, Unit>> = mutableMapOf(),
  private val eventHandlers: MutableMap<KClass<*>, List<KSuspendFunction2<Event, UnitOfWork, Unit>>> = mutableMapOf()
) : MessageBus {

  private val logger = KotlinLogging.logger {}

  override fun copy() = DefaultMessageBus(commandHandlers, eventHandlers)

  @Suppress("UNCHECKED_CAST")
  override fun addEventHandler(eventClass: KClass<*>, handler: Any): DefaultMessageBus {
    val handlers = eventHandlers.getOrDefault(eventClass, emptyList())
    if (handlers.contains(handler)) throw RuntimeException("Event handler has already been registered")
    eventHandlers[eventClass] = handlers.plus(handler as KSuspendFunction2<Event, UnitOfWork, Unit>)
    return this
  }

  override fun addEventHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus {
    pairs.forEach {
      addEventHandler(it.first, it.second)
    }
    return this
  }

  @Suppress("UNCHECKED_CAST")
  override fun addCommandHandler(commandClass: KClass<*>, handler: Any): DefaultMessageBus {
    if (commandHandlers.containsKey(commandClass)) throw RuntimeException("Command handler already exists")
    commandHandlers[commandClass] = handler as KSuspendFunction2<Command, UnitOfWork, Unit>
    return this
  }

  override fun addCommandHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus {
    pairs.forEach {
      addCommandHandler(it.first, it.second)
    }
    return this
  }

  override suspend fun handle(message: Message, unitOfWork: UnitOfWork) {
    val queue = mutableListOf(message)
    while (queue.size > 0) {
      when (val msg = queue.removeAt(0)) {
        is Event -> handleEvent(msg, queue, unitOfWork)
        is Command -> handleCommand(msg, queue, unitOfWork)
        else -> throw RuntimeException("Message is not of type Command or Event but was ${msg::class.simpleName}")
      }
    }
  }

  private suspend fun handleEvent(event: Event, queue: MutableList<Message>, unitOfWork: UnitOfWork) =
    eventHandlers.getOrDefault(event::class, emptyList()).forEach {
      try {
        it(event, unitOfWork)
        queue.addAll(unitOfWork.events)
      } catch (e: Exception) {
        logger.error(e) { "Unable to handle event" }
        throw e
      }
    }

  private suspend fun handleCommand(command: Command, queue: MutableList<Message>, unitOfWork: UnitOfWork) =
    commandHandlers[command::class]?.also {
      it(command, unitOfWork)
      queue.addAll(unitOfWork.events)
    } ?: run {
      throw RuntimeException("Unable to handle command of type ${command::class.simpleName}")
    }
}
