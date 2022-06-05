package com.acme.web.server.scheduling.data

data class ClientRecord(
  val id: String,
  val names: List<HumanNameRecord> = emptyList(),
  val gender: String,
  val contactPoints: List<ContactPointRecord> = emptyList(),
)
