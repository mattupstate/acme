package com.acme.scheduling

import com.acme.core.HasRevision
import com.acme.core.HasRevision.Companion.MIN_REVISION_NUMBER
import com.acme.core.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Client(
  override val id: Id,
  val names: Set<HumanName>,
  val gender: Gender,
  val contactPoints: Set<ContactPoint> = emptySet(),
  override val revision: Int = MIN_REVISION_NUMBER
) : Identifiable<Client.Id>, HasRevision {

  override fun equals(other: Any?): Boolean =
    when (other) {
      is Client -> {
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
