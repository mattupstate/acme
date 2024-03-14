package com.acme.core

import kotlin.reflect.KClass

interface MessageBus {
  fun copy(): DefaultMessageBus
  fun addEventHandler(eventClass: KClass<out Event>, handler: Any): DefaultMessageBus
  fun addEventHandler(vararg pairs: Pair<KClass<out Event>, Any>): DefaultMessageBus
  fun addCommandHandler(commandClass: KClass<out Command>, handler: Any): DefaultMessageBus
  fun addCommandHandler(vararg pairs: Pair<KClass<out Command>, Any>): DefaultMessageBus
  suspend fun handle(message: Message, unitOfWork: UnitOfWork)
}
