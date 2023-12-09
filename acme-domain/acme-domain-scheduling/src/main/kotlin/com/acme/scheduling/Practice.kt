package com.acme.scheduling

import com.acme.core.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Practice(
  override val id: Id,
  val owner: Practitioner.Id,
  val name: Name,
  val contactPoints: Set<ContactPoint>,
) : Identifiable<Practice.Id> {
  
  @JvmInline
  @Serializable
  value class Id(val value: String)

  @JvmInline
  @Serializable
  value class Name(val value: String)
}
