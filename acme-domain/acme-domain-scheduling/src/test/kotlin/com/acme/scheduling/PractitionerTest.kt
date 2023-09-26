package com.acme.scheduling

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.ShouldSpec

@Ignored
class PractitionerTest : ShouldSpec({

  val fixture = Practitioner(
    id = Practitioner.Id("Practitioner123"),
    user = UserId("User123"),
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
