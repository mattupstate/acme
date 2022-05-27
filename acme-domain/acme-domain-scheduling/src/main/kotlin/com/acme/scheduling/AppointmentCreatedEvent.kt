package com.acme.scheduling

import com.acme.core.MetaData

data class AppointmentCreatedEvent(
  val appointmentId: Appointment.Id,
  val period: Period,
  val clientId: Client.Id,
  val practitionerId: Practitioner.Id,
  val practiceId: Practice.Id,
  val state: AppointmentState
) : SchedulingDomainEvent {
  override val metadata: MetaData = MetaData()
}
