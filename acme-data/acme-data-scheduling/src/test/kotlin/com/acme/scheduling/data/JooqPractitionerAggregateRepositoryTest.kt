package com.acme.scheduling.data

import com.acme.scheduling.Gender
import com.acme.scheduling.HumanName
import com.acme.scheduling.Name
import com.acme.scheduling.Period
import com.acme.scheduling.Practitioner
import com.acme.scheduling.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class JooqPractitionerAggregateRepositoryTest : ShouldSpec({

  val jooq = listener(JooqAndPostgresListener())

  val practitioner = Practitioner(
    id = Practitioner.Id("Practitioner123"),
    user = UserId("User123"),
    names = setOf(
      HumanName(
        given = Name.Given("Practitioner"),
        family = Name.Family("Jones"),
        prefix = Name.Prefix(""),
        suffix = Name.Suffix(""),
        period = Period.Unknown
      )
    ),
    gender = Gender.UNKNOWN
  )

  should("save new aggregate") {
    jooq.testTransaction {
      val repo = JooqPractitionerAggregateRepository(it.dsl())
      repo.save(practitioner)
      repo.get(practitioner.id).shouldBe(practitioner)
      repo.exists(practitioner.id).shouldBeTrue()
    }
  }

  should("update existing aggregate") {
    jooq.testTransaction {
      val repo = JooqPractitionerAggregateRepository(it.dsl())
      repo.save(practitioner)
      val updatedPractitioner = practitioner.copy(gender = Gender.FEMALE, revision = 2)
      repo.save(updatedPractitioner)
      repo.get(practitioner.id).shouldBe(updatedPractitioner)
    }
  }

  should("throw NoSuchElementException") {
    jooq.testTransaction {
      val repo = JooqPractitionerAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.get(practitioner.id)
      }
    }
  }

  should("throw user supplied exception") {
    jooq.testTransaction {
      val repo = JooqPractitionerAggregateRepository(it.dsl())
      shouldThrow<FakeException> {
        repo.getOrThrow(practitioner.id) {
          FakeException()
        }
      }
    }
  }
})
