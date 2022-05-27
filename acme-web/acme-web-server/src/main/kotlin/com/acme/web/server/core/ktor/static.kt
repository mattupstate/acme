package com.acme.web.server.core.ktor

import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.routing.Route

fun Route.swaggerUI() {
  static("swagger-ui") {
    resources("swagger-ui")
    defaultResource("swagger-ui/index.html")
  }
}

fun Route.docs() {
  static("docs") {
    resources("asciidoc")
    defaultResource("asciidoc/index.html")
  }
}

fun Route.staticAssets() {
  swaggerUI()
  docs()
}
