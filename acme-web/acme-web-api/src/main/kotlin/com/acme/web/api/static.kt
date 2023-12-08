package com.acme.web.api

import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.routing.Route

fun Route.swaggerUI() {
  static("spec") {
    resources("swagger-ui")
    defaultResource("swagger-ui/index.html")
  }
}

fun Route.staticAssets() {
  swaggerUI()
}
