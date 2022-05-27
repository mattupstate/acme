package com.acme.web.server.scheduling.data

data class ClientRecord(
  val id: String,
  val names: Set<HumanNameRecord> = emptySet(),
  val gender: String,
  val contactPoints: Set<ContactPointRecord> = emptySet(),
)
