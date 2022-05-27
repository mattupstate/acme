package com.acme.core

import kotlin.reflect.KClass

interface MessageBus {
  fun copy(): DefaultMessageBus
  fun addEventHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus
  fun addCommandHandler(vararg pairs: Pair<KClass<*>, Any>): DefaultMessageBus
  fun handle(message: Message, unitOfWork: UnitOfWork)
}
