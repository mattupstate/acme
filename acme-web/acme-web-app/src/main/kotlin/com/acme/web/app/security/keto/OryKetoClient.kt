package com.acme.web.app.security.keto

interface OryKetoClient {
  suspend fun list(tuple: RelationTuple, pageSize: Int): RelationTuplesCollection

  suspend fun list(
    tuple: RelationTuple,
    pageSize: Int,
    nextPageToken: String
  ): RelationTuplesCollection

  suspend fun check(tuple: RelationTuple): CheckResponse

  suspend fun expand(tuple: RelationTuple): ExpandTree

  suspend fun expand(tuple: RelationTuple, maxDepth: Int): ExpandTree

  suspend fun create(tuple: RelationTuple): RelationTuple

  suspend fun delete(tuple: RelationTuple)

  companion object {
    const val TUPLES_COLLECTION_URL = "/relation-tuples"
    const val CHECK_URL = "/check"
    const val EXPAND_URL = "/expand"
  }
}
