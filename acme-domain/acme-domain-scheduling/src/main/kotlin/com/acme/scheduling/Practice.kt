package com.acme.scheduling

import com.acme.core.HasRevision
import com.acme.core.HasRevision.Companion.MIN_REVISION_NUMBER
import com.acme.core.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Practice(
  override val id: Id,
  val owner: Practitioner.Id,
  val name: Name,
  val contactPoints: Set<ContactPoint>,
  override val revision: Int = MIN_REVISION_NUMBER
) : Identifiable<Practice.Id>, HasRevision {

  override fun equals(other: Any?): Boolean =
    when (other) {
      is Practice -> {
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

  @JvmInline
  @Serializable
  value class Name(val value: String)
}
