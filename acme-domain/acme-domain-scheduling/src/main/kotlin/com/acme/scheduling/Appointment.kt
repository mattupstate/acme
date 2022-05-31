package com.acme.scheduling

import com.acme.core.HasRevision
import com.acme.core.HasRevision.Companion.MIN_REVISION_NUMBER
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
  override val revision: Int = MIN_REVISION_NUMBER
) : Identifiable<Appointment.Id>, HasRevision {

  fun markUnattended() =
    copy(state = AppointmentState.UNATTENDED, revision = revision + 1)

  fun markAttended() =
    copy(state = AppointmentState.ATTENDED, revision = revision + 1)

  fun cancel() =
    copy(state = AppointmentState.CANCELED, revision = revision + 1)

  override fun equals(other: Any?): Boolean =
    when (other) {
      is Appointment -> {
        other.id == id
      }
      else -> false
    }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  @JvmInline
  @Serializable
  value class Id(val value: String)
}
