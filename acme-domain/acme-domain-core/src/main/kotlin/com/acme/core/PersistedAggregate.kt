package com.acme.core

data class PersistedAggregate<T>(
  val aggregate: T,
  val metaData: PersistenceMetaData
)
