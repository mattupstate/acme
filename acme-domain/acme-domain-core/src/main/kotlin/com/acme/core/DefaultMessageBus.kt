package com.acme.core

import mu.KotlinLogging
import kotlin.reflect.KClass

class DefaultMessageBus(
  private val commandHandlers: MutableMap<KClass<*>, (Command, UnitOfWork) -> Unit> = mutableMapOf(),
  private val eventHandlers: MutableMap<KClass<*>, List<(Event, UnitOfWork) -> Unit>> = mutableMapOf()
) : MessageBus {

  private val logger = KotlinLogging.logger {}

  override fun copy() = DefaultMessageBus(commandHandlers, eventHandlers)

  @Suppress("UNCHECKED_CAST")
  override fun addEventHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus {
    pairs.forEach {
      val handler = it.second as (Event, UnitOfWork) -> Unit
      val handlers = eventHandlers.getOrDefault(it.first, emptyList())
      if (handlers.contains(it.second)) throw RuntimeException("${it.second} has already been registered")
      eventHandlers[it.first] = handlers.plus(handler)
    }
    return this
  }

  @Suppress("UNCHECKED_CAST")
  override fun addCommandHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus {
    pairs.forEach {
      if (commandHandlers.containsKey(it.first)) throw RuntimeException("${it.first} handler already exists")
      commandHandlers[it.first] = it.second as (Command, UnitOfWork) -> Unit
    }
    return this
  }

  override fun handle(message: Message, unitOfWork: UnitOfWork) {
    val queue = mutableListOf(message)
    while (queue.size > 0) {
      when (val msg = queue.removeAt(0)) {
        is Event -> handleEvent(msg, queue, unitOfWork)
        is Command -> handleCommand(msg, queue, unitOfWork)
        else -> throw RuntimeException("Message is not of type Command or Event but was ${msg::class.simpleName}")
      }
    }
  }

  private fun handleEvent(event: Event, queue: MutableList<Message>, unitOfWork: UnitOfWork) =
    eventHandlers.getOrDefault(event::class, emptyList()).forEach {
      try {
        it(event, unitOfWork)
        queue.addAll(unitOfWork.events)
      } catch (e: Exception) {
        logger.error("Unable to handle event", e)
        throw e
      }
    }

  private fun handleCommand(command: Command, queue: MutableList<Message>, unitOfWork: UnitOfWork) =
    commandHandlers[command::class]?.also {
      it(command, unitOfWork)
      queue.addAll(unitOfWork.events)
    } ?: run {
      throw RuntimeException("Unable to handle command of type ${command::class.simpleName}")
    }
}
