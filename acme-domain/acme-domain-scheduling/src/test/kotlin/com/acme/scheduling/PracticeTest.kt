package com.acme.scheduling

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class PracticeTest : ShouldSpec({
  val fixture = Practice(
    id = Practice.Id("Practice123"),
    name = Practice.Name("Somebody & Associates"),
    owner = Practitioner.Id("Practitioner123"),
    contactPoints = setOf()
  )

  should("be equal when having the same ID") {
    val fixture2 = fixture.copy()
    fixture2.shouldBe(fixture)

    val fixture3 = fixture.copy(id = Practice.Id("Blah"))
    fixture3.shouldNotBe(fixture)
  }

  should("have an initial revision of 1") {
    val practice = Practice(
      id = Practice.Id("Practice123"),
      name = Practice.Name("Somebody & Associates"),
      owner = Practitioner.Id("Practitioner123"),
      contactPoints = setOf()
    )

    practice.revision.shouldBe(1)
  }
})
