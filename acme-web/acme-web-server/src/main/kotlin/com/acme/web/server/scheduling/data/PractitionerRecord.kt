package com.acme.web.server.scheduling.data

data class PractitionerRecord(
  val id: String,
  val names: Set<HumanNameRecord>,
  val gender: String,
  val contactPoints: Set<ContactPointRecord>,
)
