// package com.acme.web.test
//
// import com.acme.web.client.ClientSideException
// import com.acme.web.server.json.hal.HalLink
// import com.acme.web.json.hal.VndError
// import com.acme.web.test.AcmeWebTestProjectConfig.Companion.serverListener
// import io.kotest.assertions.throwables.shouldThrow
// import io.kotest.matchers.shouldBe
// import io.ktor.client.request.post
// import io.ktor.client.statement.HttpResponse
// import kotlinx.serialization.Serializable
//
// class UnexpectedJsonRequestSpec : ApiSpec({
//   should("return error with invalid nested data") {
//     val exc = shouldThrow<ClientSideException.ValidationException> {
//       serverListener.http.post<HttpResponse>("/practitioners") {
//         body = MockBackRequest(
//           name = MockBadName(emptyMap(), ""),
//           gender = "UNKNOWN",
//           phoneNumbers = listOf(
//             "917-555-5555"
//           ),
//           emailAddresses = listOf(
//             "hello@world.com"
//           ),
//         )
//       }
//     }
//
//     exc.error shouldBe VndError(
//       VndError.Links(
//         help = HalLink("/docs/help.html#validation_error"),
//         about = HalLink("/scheduling/practitioners")
//       ),
//       message = "Validation error",
//       total = 1,
//       embedded = VndError.Embedded(
//         errors = listOf(
//           VndError(
//             links = VndError.Links(
//               help = HalLink("/docs/help.html#request_decoding_error")
//             ),
//             message = "Unexpected JSON token at offset 27: Expected beginning of the string, but got {"
//           )
//         )
//       )
//     )
//   }
// }) {
//   @Serializable
//   data class MockBadName(
//     val given: Map<String, String>,
//     val family: String
//   )
//
//   @Serializable
//   data class MockBackRequest(
//     val name: MockBadName,
//     val gender: String,
//     val phoneNumbers: List<String>,
//     val emailAddresses: List<String>,
//   )
// }
