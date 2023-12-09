package com.acme.scheduling

import com.acme.core.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
  override val id: Id,
  val period: Period,
  val client: Client.Id,
  val practitioner: Practitioner.Id,
  val practice: Practice.Id,
  val state: AppointmentState,
) : Identifiable<Appointment.Id> {

  fun markUnattended() =
    copy(state = AppointmentState.UNATTENDED)

  fun markAttended() =
    copy(state = AppointmentState.ATTENDED)

  fun cancel() =
    copy(state = AppointmentState.CANCELED)

  @JvmInline
  @Serializable
  value class Id(val value: String)
}
