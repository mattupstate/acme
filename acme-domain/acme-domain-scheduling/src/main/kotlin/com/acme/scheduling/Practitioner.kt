package com.acme.scheduling

import com.acme.core.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Practitioner(
  override val id: Id,
  val user: UserId,
  val names: Set<HumanName>,
  val gender: Gender,
  val contactPoints: Set<ContactPoint> = emptySet(),
) : Identifiable<Practitioner.Id> {
  
  @JvmInline
  @Serializable
  value class Id(val value: String)
}
