package com.acme.scheduling

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.ShouldSpec
@Ignored
class PracticeTest : ShouldSpec({
  val fixture = Practice(
    id = Practice.Id("Practice123"),
    name = Practice.Name("Somebody & Associates"),
    owner = Practitioner.Id("Practitioner123"),
    contactPoints = setOf()
  )
})
