package com.acme.web.server.scheduling.data

data class PractitionerRecord(
  val id: String,
  val names: List<HumanNameRecord>,
  val gender: String,
  val contactPoints: List<ContactPointRecord>,
)
