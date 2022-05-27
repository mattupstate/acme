package com.acme.scheduling.data

import com.acme.scheduling.Appointment
import com.acme.scheduling.AppointmentState
import com.acme.scheduling.Client
import com.acme.scheduling.Gender
import com.acme.scheduling.HumanName
import com.acme.scheduling.Name
import com.acme.scheduling.Period
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import com.acme.scheduling.UserId
import com.acme.test.IntegrationTest
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.time.LocalDateTime

class JooqSchedulingUnitOfWorkTest : ShouldSpec({
  tags(IntegrationTest)

  val jooq = listener(JooqAndPostgresListener())

  should("commit work") {
    jooq.testTransaction {
      val uow = SchedulingJooqUnitOfWork(it)
      runBlocking {
        uow.transaction {
          with(uow.repositories) {
            practices.save(
              Practice(
                id = Practice.Id("Practice123"),
                name = Practice.Name("Hello & Associates"),
                contactPoints = emptySet(),
                owner = Practitioner.Id("Practitioner123")
              )
            )

            appointments.save(
              Appointment(
                id = Appointment.Id("Appointment123"),
                practitioner = Practitioner.Id("Practitioner123"),
                client = Client.Id("Client123"),
                practice = Practice.Id("Practice123"),
                period = Period.Bounded(LocalDateTime.now(), LocalDateTime.now().plusSeconds(100)),
                state = AppointmentState.SCHEDULED
              )
            )

            clients.save(
              Client(
                id = Client.Id("Client123"),
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
            )

            practitioners.save(
              Practitioner(
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
            )
          }
        }
        val uow2 = SchedulingJooqUnitOfWork(it)
        uow2.transaction {
          with(uow2.repositories) {
            appointments.exists(Appointment.Id("Appointment123")).shouldBeTrue()
            clients.exists(Client.Id("Client123")).shouldBeTrue()
            practices.exists(Practice.Id("Practice123")).shouldBeTrue()
            practitioners.exists(Practitioner.Id("Practitioner123")).shouldBeTrue()
          }
        }
      }
    }
  }
})
