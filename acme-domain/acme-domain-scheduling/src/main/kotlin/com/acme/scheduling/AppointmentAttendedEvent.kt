package com.acme.scheduling

import com.acme.core.MetaData

data class AppointmentAttendedEvent(
  val appointmentId: Appointment.Id
) : SchedulingDomainEvent {
  override val metadata: MetaData = MetaData()
}
