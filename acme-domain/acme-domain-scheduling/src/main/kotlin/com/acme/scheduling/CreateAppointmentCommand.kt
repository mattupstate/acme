package com.acme.scheduling

import com.acme.core.Command
import com.acme.core.MetaData

data class CreateAppointmentCommand(
  val id: Appointment.Id,
  val practitioner: Practitioner.Id,
  val client: Client.Id,
  val practice: Practice.Id,
  val state: AppointmentState,
  val period: Period
) : Command {
  override val metadata: MetaData = MetaData()
}
