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
  
  val jooq = listener(JooqAndPostgresListener())

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
    jooq.testTransaction {
      val repo = JooqClientAggregateRepository(it.dsl())
      repo.save(client)
      repo.get(client.id).shouldBe(client)
      repo.exists(client.id).shouldBeTrue()
    }
  }

  should("should update existing aggregate") {
    jooq.testTransaction {
      val repo = JooqClientAggregateRepository(it.dsl())
      repo.save(client)
      val updatedClient = client.copy(revision = 2, gender = Gender.FEMALE)
      repo.save(updatedClient)
      repo.get(client.id).shouldBe(updatedClient)
    }
  }

  should("should throw NoSuchElementException") {
    jooq.testTransaction {
      val repo = JooqClientAggregateRepository(it.dsl())
      shouldThrow<NoSuchElementException> {
        repo.get(client.id)
      }
    }
  }

  should("should throw user supplied exception") {
    jooq.testTransaction {
      val repo = JooqClientAggregateRepository(it.dsl())
      shouldThrow<FakeException> {
        repo.getOrThrow(client.id) {
          FakeException()
        }
      }
    }
  }
})
