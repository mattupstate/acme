package com.acme.web.app.views

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.appointments() {
  get("/appointments") {
    call.respondTemplate("appointments")
  }
}
