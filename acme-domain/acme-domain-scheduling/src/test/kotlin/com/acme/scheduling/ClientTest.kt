package com.acme.scheduling

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.ShouldSpec

@Ignored
class ClientTest : ShouldSpec({
  val fixture = Client(
    id = Client.Id("Client123"),
    names = setOf(
      HumanName(
        family = Name.Family("World"),
        given = Name.Given("Hello"),
        prefix = Name.Prefix(""),
        suffix = Name.Suffix(""),
        period = Period.Unknown
      )
    ),
    gender = Gender.UNKNOWN,
    contactPoints = emptySet()
  )

})
