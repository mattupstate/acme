@file:OptIn(ExperimentalSerializationApi::class)
package com.acme.web.server.core

import am.ik.timeflake.Timeflake
import jakarta.validation.ElementKind
import jakarta.validation.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

val defaultIdGenerator: () -> String = { Timeflake.generate().base62() }

val defaultJson = Json {
  prettyPrint = true
  prettyPrintIndent = "  "
}

fun Path.toJsonPointer(): String = "/" + map {
  when (it.kind) {
    ElementKind.PROPERTY -> it.name
    ElementKind.CONTAINER_ELEMENT -> it.index
    else -> throw RuntimeException("Unsupported ElementKind: ${it.kind}")
  }
}.joinToString("/")
