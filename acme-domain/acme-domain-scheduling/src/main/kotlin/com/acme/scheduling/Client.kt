package com.acme.scheduling

import com.acme.core.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Client(
  override val id: Id,
  val names: Set<HumanName>,
  val gender: Gender,
  val contactPoints: Set<ContactPoint> = emptySet(),
) : Identifiable<Client.Id> {
  override fun hashCode(): Int {
    return id.hashCode()
  }

  @JvmInline
  @Serializable
  value class Id(val value: String)
}
