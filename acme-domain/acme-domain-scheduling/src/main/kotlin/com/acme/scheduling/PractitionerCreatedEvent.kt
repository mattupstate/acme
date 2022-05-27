package com.acme.scheduling

import com.acme.core.MetaData

data class PractitionerCreatedEvent(
  val practitioner: Practitioner
) : SchedulingDomainEvent {
  override val metadata: MetaData = MetaData()
}
