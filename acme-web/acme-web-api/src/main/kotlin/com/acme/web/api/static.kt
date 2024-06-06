package com.acme.web.api

import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Route
import java.io.File

fun Route.swaggerUI() {
  staticFiles("spec", File("swagger-ui"))
  staticResources("spec", "swagger-ui")
}

fun Route.staticAssets() {
  swaggerUI()
}
