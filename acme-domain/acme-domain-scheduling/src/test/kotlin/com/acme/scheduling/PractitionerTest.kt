package com.acme.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

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

  should("be equal when having the same ID") {
    val fixture2 = fixture.copy()
    fixture2.shouldBe(fixture)

    val fixture3 = fixture.copy(id = Practitioner.Id("Blah"))
    fixture3.shouldNotBe(fixture)
  }

  should("have an initial revision of 1") {
    val practitioner = Practitioner(
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

    practitioner.revision.shouldBe(1)
  }
})
