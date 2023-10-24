package com.acme.web.app.views

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.health() {
  get("/health/check") {
    call.respondText("OK")
  }
}
