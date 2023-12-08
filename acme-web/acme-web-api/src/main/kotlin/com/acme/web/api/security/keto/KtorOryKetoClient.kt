package com.acme.web.api.security.keto

import com.acme.web.api.security.keto.OryKetoClient.Companion.CHECK_URL
import com.acme.web.api.security.keto.OryKetoClient.Companion.EXPAND_URL
import com.acme.web.api.security.keto.OryKetoClient.Companion.TUPLES_COLLECTION_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.parametersOf

class KtorOryKetoClient(private val readClient: HttpClient, private val writeClient: HttpClient) : OryKetoClient {

  constructor(baseUrl: String, readPort: Int, writePort: Int) : this(
    HttpClient {
      defaultOryKetoClientConfiguration("$baseUrl:$readPort")
    },
    HttpClient {
      defaultOryKetoClientConfiguration("$baseUrl:$writePort")
    }
  )

  private fun optionalQueryParams(tuple: RelationTuple): Array<Pair<String, List<String>>> =
    mutableListOf<Pair<String, List<String>>>().apply {
      if (tuple.subjectSet != null) {
        addAll(
          listOf(
            "subject_set.namespace" to listOf(tuple.subjectSet.namespace),
            "subject_set.object" to listOf(tuple.subjectSet.obj),
            "subject_set.relation" to listOf(tuple.subjectSet.relation),
          )
        )
      }
      if (tuple.subjectId != null) {
        addAll(
          listOf(
            "subject_id" to listOf(tuple.subjectId)
          )
        )
      }
    }.toTypedArray()

  override suspend fun list(tuple: RelationTuple, pageSize: Int): RelationTuplesCollection =
    readClient.get(TUPLES_COLLECTION_URL) {
      parametersOf(
        "namespace" to listOf(tuple.namespace),
        "object" to listOf(tuple.obj),
        "relation" to listOf(tuple.relation),
        "page_size" to listOf(pageSize.toString()),
        *optionalQueryParams(tuple)
      )
    }.body()

  override suspend fun list(tuple: RelationTuple, pageSize: Int, nextPageToken: String): RelationTuplesCollection =
    readClient.get(TUPLES_COLLECTION_URL) {
      parametersOf(
        "namespace" to listOf(tuple.namespace),
        "object" to listOf(tuple.obj),
        "relation" to listOf(tuple.relation),
        "page_size" to listOf(pageSize.toString()),
        "page_token" to listOf(nextPageToken),
        *optionalQueryParams(tuple)
      )
    }.body()

  override suspend fun check(tuple: RelationTuple): CheckResponse =
    readClient.post(CHECK_URL) {
      setBody(tuple)
    }.body()

  override suspend fun expand(tuple: RelationTuple): ExpandTree =
    readClient.get(EXPAND_URL) {
      parametersOf(
        "namespace" to listOf(tuple.namespace),
        "object" to listOf(tuple.obj),
        "relation" to listOf(tuple.relation),
      )
    }.body()

  override suspend fun expand(tuple: RelationTuple, maxDepth: Int): ExpandTree =
    readClient.get(EXPAND_URL) {
      parametersOf(
        "namespace" to listOf(tuple.namespace),
        "object" to listOf(tuple.obj),
        "relation" to listOf(tuple.relation),
        "max_depth" to listOf(maxDepth.toString())
      )
    }.body()

  override suspend fun create(tuple: RelationTuple): RelationTuple =
    writeClient.put(TUPLES_COLLECTION_URL) {
      setBody(tuple)
    }.body()

  override suspend fun delete(tuple: RelationTuple) {
    writeClient.delete(TUPLES_COLLECTION_URL) {
      parametersOf(
        "namespace" to listOf(tuple.namespace),
        "object" to listOf(tuple.obj),
        "relation" to listOf(tuple.relation),
        *optionalQueryParams(tuple),
      )
    }
  }
}
