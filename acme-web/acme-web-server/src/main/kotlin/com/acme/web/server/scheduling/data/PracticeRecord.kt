package com.acme.web.server.scheduling.data

data class PracticeRecord(
  val id: String,
  val name: String,
  val contactPoints: Set<ContactPointRecord>
)
