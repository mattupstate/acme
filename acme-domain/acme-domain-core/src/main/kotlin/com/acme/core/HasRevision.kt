package com.acme.core

interface HasRevision {
  val revision: Int

  companion object {
    const val MIN_REVISION_NUMBER = 1

    fun aggregateIsNew(agg: HasRevision) =
      agg.revision == MIN_REVISION_NUMBER
  }
}
