package com.acme.scheduling

import kotlinx.serialization.Serializable

@Serializable
sealed class Name {
  abstract val value: String

  @Serializable
  data class Family(override val value: String) : Name()

  @Serializable
  data class Given(override val value: String) : Name()

  @Serializable
  data class Prefix(override val value: String) : Name()

  @Serializable
  data class Suffix(override val value: String) : Name()
}
