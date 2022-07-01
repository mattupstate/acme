package com.acme.web.server.test

import com.acme.test.EndToEndTest
import io.kotest.core.spec.style.ShouldSpec
import io.ktor.http.HeadersBuilder

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

abstract class ApiSpec(
  body: ShouldSpec.() -> Unit
) : ShouldSpec({
  tags(EndToEndTest)
  body()
})
