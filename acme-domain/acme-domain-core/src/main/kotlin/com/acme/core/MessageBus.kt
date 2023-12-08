package com.acme.core

import kotlin.reflect.KClass

interface MessageBus {
  fun copy(): DefaultMessageBus
  fun addEventHandler(eventClass: KClass<*>, handler: Any): DefaultMessageBus
  fun addEventHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus
  fun addCommandHandler(commandClass: KClass<*>, handler: Any): DefaultMessageBus
  fun addCommandHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus
  suspend fun handle(message: Message, unitOfWork: UnitOfWork)
}
