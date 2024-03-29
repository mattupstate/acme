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
    testTransaction {
      val time: TimeFixture = timeFixtureFactory()
      val repo = JooqPractitionerAggregateRepository(it.dsl(), time.clock)
      repo.save(practitioner)
      repo.exists(practitioner.id).shouldBeTrue()

      val persistedPractitioner = repo.findById(practitioner.id).getOrThrow()
      persistedPractitioner.aggregate.shouldBe(practitioner)
      with(persistedPractitioner.metaData) {
        revision.shouldBe(1)
        createdAt.shouldBe(time.now)
        updatedAt.shouldBe(time.now)
      }
    }
  }

  should("update existing aggregate and increment revision") {
    testTransaction {
      val createTime = timeFixtureFactory()
      val createRepo = JooqPractitionerAggregateRepository(it.dsl(), createTime.clock)
      createRepo.save(practitioner)

      val updateTime = timeFixtureFactory()
      val updateRepo = JooqPractitionerAggregateRepository(it.dsl(), updateTime.clock)
      val expectedPractitioner = practitioner.copy(gender = Gender.FEMALE)
      updateRepo.save(expectedPractitioner)

      val persistedPractitioner = createRepo.findById(practitioner.id).getOrThrow()
      persistedPractitioner.aggregate.shouldBe(expectedPractitioner)
      with(persistedPractitioner.metaData) {
        revision.shouldBe(2)
        createdAt.shouldBe(createTime.now)
        updatedAt.shouldBe(updateTime.now)
      }
    }
  }

  should("throw NoSuchElementException") {
    testTransaction {
      val repo = JooqPractitionerAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.findById(practitioner.id).getOrThrow()
      }
    }
  }
})
