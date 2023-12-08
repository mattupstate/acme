package com.acme.scheduling.data

import com.acme.scheduling.ContactPoint
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class JooqPracticeAggregateRepositoryTest : ShouldSpec({

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
    testTransaction {
      val time: TimeFixture = timeFixtureFactory()
      val repo = JooqPracticeAggregateRepository(it.dsl(), time.clock)
      repo.save(practice)
      repo.exists(practice.id).shouldBeTrue()

      val persistedPractice = repo.get(practice.id)
      persistedPractice.aggregate.shouldBe(practice)
      persistedPractice.metaData.revision.shouldBe(1)
      persistedPractice.metaData.createdAt.shouldBe(time.now)
      persistedPractice.metaData.updatedAt.shouldBe(time.now)
    }
  }

  should("update an existing aggregate and increment revision") {
    testTransaction {
      val createTime = timeFixtureFactory()
      val createRepo = JooqPracticeAggregateRepository(it.dsl(), createTime.clock)
      createRepo.save(practice)

      val updateTime = timeFixtureFactory()
      val updateRepo = JooqPracticeAggregateRepository(it.dsl(), updateTime.clock)
      val expectedPractice = practice.copy(
        name = Practice.Name("Smith & Associates"),
      )
      updateRepo.save(expectedPractice)

      val persistedPractice = createRepo.get(practice.id)
      persistedPractice.aggregate.shouldBe(expectedPractice)
      persistedPractice.metaData.revision.shouldBe(2)
      persistedPractice.metaData.createdAt.shouldBe(createTime.now)
      persistedPractice.metaData.updatedAt.shouldBe(updateTime.now)
    }
  }

  should("throw NoSuchElementException") {
    testTransaction {
      val repo = JooqPracticeAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.get(practice.id)
      }
    }
  }

  should("throw user supplied exception") {
    testTransaction {
      val repo = JooqPracticeAggregateRepository(it.dsl())
      shouldThrow<FakeException> {
        repo.getOrThrow(practice.id) {
          FakeException()
        }
      }
    }
  }
})
