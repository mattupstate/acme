package com.acme.scheduling.data

import com.acme.scheduling.ContactPoint
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import com.acme.test.IntegrationTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class JooqPracticeAggregateRepositoryTest : ShouldSpec({
  tags(IntegrationTest)
  val jooq = listener(JooqAndPostgresListener())

  val practice = Practice(
    id = Practice.Id("PracticeID"),
    name = Practice.Name("Practice & Associates"),
    owner = Practitioner.Id("Practitioner123"),
    contactPoints = setOf(
      ContactPoint.Phone.Unverified("666-666-6666"),
      ContactPoint.Phone.Verified("555-555-5555"),
      ContactPoint.SMS.Unverified("444-444-4444"),
      ContactPoint.SMS.Verified("333-333-3333"),
      ContactPoint.Email.Unverified("hello@world.com"),
      ContactPoint.Email.Verified("goodbye@jupiter.com"),
      ContactPoint.Web("https://somewhere.com")
    )
  )

  should("save new aggregate") {
    jooq.testTransaction {
      val repo = JooqPracticeAggregateRepository(it.dsl())
      repo.save(practice)
      val persistedPractice = repo.get(practice.id)
      persistedPractice.shouldBe(practice)
      repo.exists(practice.id).shouldBeTrue()
    }
  }

  should("update an existing aggregate") {
    jooq.testTransaction {
      val repo = JooqPracticeAggregateRepository(it.dsl())
      repo.save(practice)
      val updatedPractice = practice.copy(
        name = Practice.Name("Smith & Associates"),
        revision = 2,
      )
      repo.save(updatedPractice)
      repo.get(practice.id).shouldBe(updatedPractice)
    }
  }

  should("throw NoSuchElementException") {
    jooq.testTransaction {
      val repo = JooqPracticeAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.get(practice.id)
      }
    }
  }

  should("throw user supplied exception") {
    jooq.testTransaction {
      val repo = JooqPracticeAggregateRepository(it.dsl())
      shouldThrow<FakeException> {
        repo.getOrThrow(practice.id) {
          FakeException()
        }
      }
    }
  }
})
