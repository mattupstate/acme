package com.acme.scheduling

import com.acme.core.MetaData

data class PracticeCreatedEvent(
  val practice: Practice
) : SchedulingDomainEvent {
  override val metadata: MetaData = MetaData()
}
