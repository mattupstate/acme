package com.acme.scheduling

import com.acme.core.MetaData

data class ClientCreatedEvent(
  val client: Client
) : SchedulingDomainEvent {
  override val metadata: MetaData = MetaData()
}
