package com.acme.core

import java.time.LocalDateTime

data class PersistenceMetaData(
  val createdAt: LocalDateTime,
  val updatedAt: LocalDateTime,
  val revision: Int
)
