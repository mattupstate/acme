// package com.acme.web.test
//
// import com.acme.web.test.AcmeWebTestProjectConfig.Companion.serverListener
// import io.kotest.matchers.shouldBe
// import io.ktor.client.request.get
// import io.ktor.client.statement.HttpResponse
// import io.ktor.http.HttpStatusCode
//
// class StaticAssetsApiSpec : ApiSpec({
//   should("serve swagger UI") {
//     serverListener.http.get<HttpResponse>("/swagger-ui/").apply {
//       status shouldBe HttpStatusCode.OK
//     }
//   }
//
//   should("serve docs") {
//     serverListener.http.get<HttpResponse>("/docs/").apply {
//       status shouldBe HttpStatusCode.OK
//     }
//   }
// })
