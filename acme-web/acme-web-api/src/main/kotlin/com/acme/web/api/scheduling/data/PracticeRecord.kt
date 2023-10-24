package com.acme.web.api.scheduling.data

data class PracticeRecord(
  val id: String,
  val name: String,
  val contactPoints: List<ContactPointRecord>
)
