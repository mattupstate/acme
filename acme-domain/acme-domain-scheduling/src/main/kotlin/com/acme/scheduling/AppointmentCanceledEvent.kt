package com.acme.scheduling

import com.acme.core.MetaData

data class AppointmentCanceledEvent(
  val appointmentId: Appointment.Id
) : SchedulingDomainEvent {
  override val metadata: MetaData = MetaData()
}
