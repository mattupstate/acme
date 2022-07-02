package com.acme.web.server.test

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.SchemaVersion
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.report.ListProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.common.runBlocking
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HeadersBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Suppress("ObjectPropertyName")
val JsonElement._links get() = this.jsonObject["_links"]!!.jsonObject
val JsonObject.self get() = this["self"]!!.jsonObject
val JsonObject.href get() = this["href"]!!.jsonPrimitive.content
val JsonObject.items get() = this["items"]!!.jsonArray.map { it.jsonObject }

private val schemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(
  ValidationConfiguration.newBuilder().setDefaultVersion(
    SchemaVersion.DRAFTV4
  ).freeze()
).freeze()

suspend fun HttpResponse.json() = Json.parseToJsonElement(body())

suspend fun HttpResponse.firstLinkedItemHref() = json()._links.items.first().href

infix fun HttpResponse.shouldEqualJson(json: String) =
  runBlocking {
    body<String>() shouldEqualJson json
  }

infix fun HttpResponse.shouldMatchJsonSchema(schema: String) =
  runBlocking {
    body<String>() shouldMatchJsonSchema schema
  }

infix fun String.shouldMatchJsonSchema(schema: String) {
  assertionCounter.inc()
  val schemaJson = JsonLoader.fromString(schema.trimIndent())
  val contentJson = JsonLoader.fromString(this)
  val report = schemaFactory.getJsonSchema(schemaJson).validate(contentJson) as ListProcessingReport
  if (!report.isSuccess) {
    failure(report.asJson().toPrettyString())
  }
}

fun HeadersBuilder.mockUser(
  id: String = "user123",
  email: String = "user@acme.com",
  givenName: String = "User",
  familyName: String = "Name",
  preferredName: String = "User Name",
  namePrefix: String = "",
  nameSuffix: String = "",
) {
  append("X-Auth-Id", id)
  append("X-Auth-Email", email)
  append("X-Auth-Name-Preferred", preferredName)
  append("X-Auth-Name-Given", givenName)
  append("X-Auth-Name-Family", familyName)
  append("X-Auth-Name-Prefix", namePrefix)
  append("X-Auth-Name-Suffix", nameSuffix)
}
