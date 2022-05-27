package com.acme.scheduling

import kotlinx.serialization.Serializable

@Serializable
sealed class ContactPoint {
  abstract val value: String

  @Serializable
  sealed class Phone : ContactPoint() {
    @Serializable
    data class Verified(override val value: String) : Phone()

    @Serializable
    data class Unverified(override val value: String) : Phone()
  }

  @Serializable
  data class Web(override val value: String) : ContactPoint()

  @Serializable
  sealed class SMS : ContactPoint() {
    @Serializable
    data class Verified(override val value: String) : SMS()

    @Serializable
    data class Unverified(override val value: String) : SMS()
  }

  @Serializable
  sealed class Email : ContactPoint() {
    @Serializable
    data class Verified(override val value: String) : Email()

    @Serializable
    data class Unverified(override val value: String) : Email()
  }
}
