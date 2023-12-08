package com.acme.scheduling.data

import com.acme.scheduling.Client
import com.acme.scheduling.Gender
import com.acme.scheduling.HumanName
import com.acme.scheduling.Name
import com.acme.scheduling.Period
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class JooqClientAggregateRepositoryTest : ShouldSpec({

  val client = Client(
    id = Client.Id("Client123"),
    names = setOf(
      HumanName(
        given = Name.Given("Client"),
        family = Name.Family("Jones"),
        prefix = Name.Prefix(""),
        suffix = Name.Suffix(""),
        period = Period.Unknown
      )
    ),
    gender = Gender.UNKNOWN
  )

  should("should save new aggregate") {
    testTransaction {
      val time: TimeFixture = timeFixtureFactory()
      val repo = JooqClientAggregateRepository(it.dsl(), time.clock)
      repo.save(client)
      repo.exists(client.id).shouldBeTrue()

      val persistedClient = repo.get(client.id)
      persistedClient.aggregate.shouldBe(client)
      persistedClient.metaData.revision.shouldBe(1)
      persistedClient.metaData.createdAt.shouldBe(time.now)
      persistedClient.metaData.updatedAt.shouldBe(time.now)
    }
  }

  should("update existing aggregate and increment revision") {
    testTransaction {
      val createTime: TimeFixture = timeFixtureFactory()
      val createRepo = JooqClientAggregateRepository(it.dsl(), createTime.clock)
      createRepo.save(client)

      val updateTime: TimeFixture = timeFixtureFactory()
      val updateRepo = JooqClientAggregateRepository(it.dsl(), updateTime.clock)
      val expectedClient = client.copy(gender = Gender.FEMALE)
      updateRepo.save(expectedClient)

      val persistedClient = updateRepo.get(client.id)
      persistedClient.aggregate.shouldBe(expectedClient)
      persistedClient.metaData.revision.shouldBe(2)
      persistedClient.metaData.createdAt.shouldBe(createTime.now)
      persistedClient.metaData.updatedAt.shouldBe(updateTime.now)
    }
  }

  should("should throw NoSuchElementException") {
    testTransaction {
      val repo = JooqClientAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.get(client.id)
      }
    }
  }

  should("should throw user supplied exception") {
    testTransaction {
      val repo = JooqClientAggregateRepository(it.dsl())
      shouldThrow<FakeException> {
        repo.getOrThrow(client.id) {
          FakeException()
        }
      }
    }
  }
})
