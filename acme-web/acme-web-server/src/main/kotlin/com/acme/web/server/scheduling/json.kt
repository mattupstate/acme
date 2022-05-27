package com.acme.web.server.scheduling

import com.acme.serialization.InstantAsLongSerializer
import com.acme.serialization.LocalDateTimeAsStringSerializer
import com.acme.validation.OneOfStrings
import com.acme.validation.PhoneNumber
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime

@Serializable
data class Period(
  @field:NotNull
  @Serializable(InstantAsLongSerializer::class)
  val start: Instant? = null,

  @field:NotNull
  @Serializable(InstantAsLongSerializer::class)
  val end: Instant? = null
)

@Serializable
data class HumanName(
  @field:NotBlank
  @field:Size(max = 100)
  val family: String? = null,

  @field:NotBlank
  @field:Size(max = 100)
  val given: String? = null,

  @field:NotNull
  @field:Size(max = 100)
  val prefix: String? = "",

  @field:NotNull
  @field:Size(max = 100)
  val suffix: String? = "",

  @field:Valid
  val period: Period? = null
)

@Serializable
data class CreateAppointmentCommandRequest(
  @field:NotNull
  val clientId: String? = null,

  @field:NotNull
  val practitionerId: String? = null,

  @field:NotNull
  val practiceId: String? = null,

  @field:NotNull
  @field:OneOfStrings(["SCHEDULED", "UNATTENDED", "ATTENDED", "CANCELED"])
  val state: String?,

  @field:NotNull
  @Serializable(LocalDateTimeAsStringSerializer::class)
  val from: LocalDateTime? = null,

  @field:NotNull
  @Serializable(LocalDateTimeAsStringSerializer::class)
  val to: LocalDateTime? = null
)

@Serializable
data class CreateClientCommandRequest(
  @field:NotNull
  @field:Valid
  val name: HumanName? = null,

  @field:NotNull
  @field:OneOfStrings(["MALE", "FEMALE", "TRANSGENDER", "NON_BINARY", "OTHER", "UNKNOWN"])
  val gender: String? = null,

  @field:NotNull
  @field:Size(min = 1, max = 100)
  val phoneNumbers: List<@NotBlank @PhoneNumber String>? = null,

  @field:NotNull
  @field:Size(min = 1, max = 100)
  val emailAddresses: List<@NotBlank @Email String>? = null
)

@Serializable
data class CreatePracticeCommandRequest(
  @field:NotBlank
  @field:Size(max = 100)
  val name: String? = null,

  @field:NotNull
  @field:Size(min = 1, max = 100)
  val phoneNumbers: List<@NotBlank @PhoneNumber String>? = null,

  @field:NotNull
  @field:Size(min = 1, max = 100)
  val emailAddresses: List<@NotBlank @Email String>? = null
)

@Serializable
data class CreatePractitionerCommandRequest(
  @field:NotNull
  @field:Valid
  val name: HumanName? = null,

  @field:NotNull
  @field:OneOfStrings(["MALE", "FEMALE", "TRANSGENDER", "NON_BINARY", "OTHER", "UNKNOWN"])
  val gender: String? = null,

  @field:NotNull
  @field:Size(min = 1, max = 100)
  val phoneNumbers: List<@NotBlank @PhoneNumber String>? = null,

  @field:NotNull
  @field:Size(min = 1, max = 100)
  val emailAddresses: List<@NotBlank @Email String>? = null
)
