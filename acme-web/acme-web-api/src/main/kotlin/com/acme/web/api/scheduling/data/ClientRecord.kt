package com.acme.web.api.scheduling.data

data class ClientRecord(
  val id: String,
  val names: List<HumanNameRecord> = emptyList(),
  val gender: String,
  val contactPoints: List<ContactPointRecord> = emptyList(),
)
