package com.acme.scheduling

import com.acme.core.Command
import com.acme.core.MetaData

data class CancelAppointmentCommand(
  val appointment: Appointment.Id
) : Command {
  override val metadata: MetaData = MetaData()
}
