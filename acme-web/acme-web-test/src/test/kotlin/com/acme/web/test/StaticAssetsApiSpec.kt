package com.acme.web.test

import com.acme.web.test.AcmeWebTestProjectConfig.Companion.apiServer
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class StaticAssetsApiSpec : ApiSpec({
  should("serve swagger UI") {
    apiServer.http.get("/spec/").also {
      it shouldHaveStatus HttpStatusCode.OK
    }
  }
})
