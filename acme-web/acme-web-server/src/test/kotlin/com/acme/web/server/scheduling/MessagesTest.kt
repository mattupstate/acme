package com.acme.web.server.scheduling

import com.acme.core.CommandValidationException
import com.acme.scheduling.Appointment
import com.acme.scheduling.AppointmentState
import com.acme.scheduling.CancelAppointmentCommand
import com.acme.scheduling.Client
import com.acme.scheduling.ContactPoint
import com.acme.scheduling.CreateAppointmentCommand
import com.acme.scheduling.CreateClientCommand
import com.acme.scheduling.CreatePracticeCommand
import com.acme.scheduling.CreatePractitionerCommand
import com.acme.scheduling.Gender
import com.acme.scheduling.HumanName
import com.acme.scheduling.MarkAppointmentAttendedCommand
import com.acme.scheduling.MarkAppointmentUnattendedCommand
import com.acme.scheduling.Name
import com.acme.scheduling.Period
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import com.acme.scheduling.UserId
import com.acme.scheduling.data.SchedulingJooqUnitOfWork
import com.acme.web.server.scheduling.data.AppointmentRecord
import com.acme.web.server.scheduling.data.ClientRecord
import com.acme.web.server.scheduling.data.ContactPointRecord
import com.acme.web.server.scheduling.data.HumanNameRecord
import com.acme.web.server.scheduling.data.JooqSchedulingWebViews
import com.acme.web.server.scheduling.data.PracticeRecord
import com.acme.web.server.scheduling.data.PractitionerRecord
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

fun Instant.toLocalDateTimeUTC(): LocalDateTime =
  LocalDateTime.ofInstant(this, ZoneOffset.UTC)

class MessagesTest : ShouldSpec({

  context("CreatePracticeCommand") {
    should("result in new PracticeEntity") {
      testTransaction {
        val uow = SchedulingJooqUnitOfWork(it)

        runBlocking {
          uow.transaction {
            // When
            schedulingMessageBus.handle(
              CreatePracticeCommand(
                id = Practice.Id("Practice123"),
                name = Practice.Name("Hello & Associates"),
                owner = Practitioner.Id("Practitioner123"),
                contactPoints = setOf(
                  ContactPoint.Phone.Unverified("917-555-5555"),
                  ContactPoint.Email.Unverified("hello@associates.com")
                )
              ),
              uow
            )

            // Then
            with(JooqSchedulingWebViews(it.dsl())) {
              findPracticeOrThrow("Practice123").shouldBe(
                PracticeRecord(
                  id = "Practice123",
                  name = "Hello & Associates",
                  contactPoints = listOf(
                    ContactPointRecord(
                      system = "Email",
                      value = "hello@associates.com",
                      verifiedAt = null
                    ),
                    ContactPointRecord(
                      system = "Phone",
                      value = "917-555-5555",
                      verifiedAt = null
                    )
                  )
                )
              )
            }
          }
        }
      }
    }
  }

  context("CreatePractitionerCommand") {
    should("result in new PractitionerEntity") {
      testTransaction {
        val uow = SchedulingJooqUnitOfWork(it)
        runBlocking {
          uow.transaction {
            // When
            schedulingMessageBus.handle(
              CreatePractitionerCommand(
                id = Practitioner.Id("Practitioner123"),
                user = UserId("User123"),
                name = HumanName(
                  given = Name.Given("First"),
                  family = Name.Family("Last"),
                  suffix = Name.Suffix(""),
                  prefix = Name.Prefix(""),
                  period = Period.Unknown
                ),
                gender = Gender.UNKNOWN,
                contactPoints = emptySet()
              ),
              uow
            )

            // Then
            with(JooqSchedulingWebViews(it.dsl())) {
              findPractitionerOrThrow("Practitioner123").shouldBe(
                PractitionerRecord(
                  id = "Practitioner123",
                  names = listOf(
                    HumanNameRecord(
                      given = "First",
                      family = "Last",
                      suffix = "",
                      prefix = "",
                      periodStart = null,
                      periodEnd = null
                    )
                  ),
                  gender = "UNKNOWN",
                  contactPoints = emptyList()
                )
              )
            }
          }
        }
      }
    }
  }

  context("CreateClientCommand") {
    should("result in new ClientEntity") {
      testTransaction {
        val uow = SchedulingJooqUnitOfWork(it)
        runBlocking {
          uow.transaction {
            // When
            schedulingMessageBus.handle(
              CreateClientCommand(
                id = Client.Id("Client123"),
                name = HumanName(
                  family = Name.Family("Last"),
                  given = Name.Given("First"),
                  prefix = Name.Prefix(""),
                  suffix = Name.Suffix(""),
                  period = Period.Unknown
                ),
                gender = Gender.UNKNOWN,
                contactPoints = setOf(
                  ContactPoint.Phone.Unverified("917-555-5555"),
                  ContactPoint.Email.Unverified("hello@world.com")
                )
              ),
              uow
            )

            // Then
            with(JooqSchedulingWebViews(it.dsl())) {
              findClientOrThrow("Client123").shouldBe(
                ClientRecord(
                  id = "Client123",
                  names = listOf(
                    HumanNameRecord(
                      family = "Last",
                      given = "First",
                      prefix = "",
                      suffix = "",
                      periodStart = null,
                      periodEnd = null,
                    )
                  ),
                  gender = "UNKNOWN",
                  contactPoints = listOf(
                    ContactPointRecord(
                      system = "Email",
                      value = "hello@world.com",
                      verifiedAt = null
                    ),
                    ContactPointRecord(
                      system = "Phone",
                      value = "917-555-5555",
                      verifiedAt = null
                    )
                  )
                )
              )
            }
          }
        }
      }
    }
  }

  context("CreateAppointmentCommand") {
    should("throw exception with invalid references") {
      testTransaction {
        val uow = SchedulingJooqUnitOfWork(it)

        runBlocking {
          uow.transaction {
            // Given
            val start = Instant.now()
            val end = start.plus(1, ChronoUnit.HOURS)

            val command = CreateAppointmentCommand(
              id = Appointment.Id("Appointment123"),
              client = Client.Id("Client123"),
              practitioner = Practitioner.Id("Practitioner123"),
              practice = Practice.Id("Practice123"),
              state = AppointmentState.SCHEDULED,
              period = Period.Bounded(
                start.toLocalDateTimeUTC(),
                end.toLocalDateTimeUTC()
              )
            )

            // When
            val exc = assertThrows<CommandValidationException> {
              schedulingMessageBus.handle(command, uow)
            }

            // Then
            exc.command.shouldBe(command)
            exc.errors.shouldBe(
              setOf(
                CommandValidationException.InvalidAggregateReferenceError(
                  fieldName = "client",
                  value = "Client123",
                  message = "Invalid client"
                ),
                CommandValidationException.InvalidAggregateReferenceError(
                  fieldName = "practitioner",
                  value = "Practitioner123",
                  message = "Invalid practitioner"
                ),
                CommandValidationException.InvalidAggregateReferenceError(
                  fieldName = "practice",
                  value = "Practice123",
                  message = "Invalid practice"
                )
              )
            )
          }
        }
      }
    }

    should("result in new AppointmentEntity") {
      testTransaction {
        val uow = SchedulingJooqUnitOfWork(it)
        runBlocking {
          uow.transaction {
            // Given
            schedulingMessageBus.handle(
              CreatePracticeCommand(
                id = Practice.Id("Practice123"),
                name = Practice.Name("Hello & Associates"),
                owner = Practitioner.Id("Practitioner123"),
                contactPoints = setOf(
                  ContactPoint.Phone.Unverified("917-555-5555"),
                  ContactPoint.Email.Unverified("hello@associates.com")
                )
              ),
              uow
            )

            schedulingMessageBus.handle(
              CreatePractitionerCommand(
                id = Practitioner.Id("Practitioner123"),
                user = UserId("User123"),
                name = HumanName(
                  given = Name.Given("First"),
                  family = Name.Family("Last"),
                  suffix = Name.Suffix(""),
                  prefix = Name.Prefix(""),
                  period = Period.Unknown
                ),
                gender = Gender.UNKNOWN,
                contactPoints = emptySet()
              ),
              uow
            )

            schedulingMessageBus.handle(
              CreateClientCommand(
                id = Client.Id("Client123"),
                name = HumanName(
                  family = Name.Family("Last"),
                  given = Name.Given("First"),
                  prefix = Name.Prefix(""),
                  suffix = Name.Suffix(""),
                  period = Period.Unknown
                ),
                gender = Gender.UNKNOWN,
                contactPoints = setOf(
                  ContactPoint.Phone.Unverified("917-555-5555"),
                  ContactPoint.Email.Unverified("hello@world.com")
                )
              ),
              uow
            )

            val start = Instant.now()
            val end = start.plus(1, ChronoUnit.HOURS)

            // When
            schedulingMessageBus.handle(
              CreateAppointmentCommand(
                id = Appointment.Id("Appointment123"),
                client = Client.Id("Client123"),
                practitioner = Practitioner.Id("Practitioner123"),
                practice = Practice.Id("Practice123"),
                state = AppointmentState.SCHEDULED,
                period = Period.Bounded(
                  start.toLocalDateTimeUTC(),
                  end.toLocalDateTimeUTC()
                )
              ),
              uow
            )

            // Then
            with(JooqSchedulingWebViews(it.dsl())) {
              findAppointmentOrThrow("Appointment123").shouldBe(
                AppointmentRecord(
                  id = "Appointment123",
                  clientId = "Client123",
                  practiceId = "Practice123",
                  practitionerId = "Practitioner123",
                  state = "SCHEDULED",
                  periodStart = start,
                  periodEnd = end,
                )
              )
            }
          }
        }
      }
    }

    context("MarkAppointmentAttendedCommand") {
      should("result in updated AppointmentEntity") {
        testTransaction {
          val uow = SchedulingJooqUnitOfWork(it)

          runBlocking {
            uow.transaction {
              // Given
              schedulingMessageBus.handle(
                CreatePracticeCommand(
                  id = Practice.Id("Practice123"),
                  name = Practice.Name("Hello & Associates"),
                  owner = Practitioner.Id("Practitioner123"),
                  contactPoints = setOf(
                    ContactPoint.Phone.Unverified("917-555-5555"),
                    ContactPoint.Email.Unverified("hello@associates.com")
                  )
                ),
                uow
              )

              schedulingMessageBus.handle(
                CreatePractitionerCommand(
                  id = Practitioner.Id("Practitioner123"),
                  user = UserId("User123"),
                  name = HumanName(
                    given = Name.Given("First"),
                    family = Name.Family("Last"),
                    suffix = Name.Suffix(""),
                    prefix = Name.Prefix(""),
                    period = Period.Unknown
                  ),
                  gender = Gender.UNKNOWN,
                  contactPoints = emptySet()
                ),
                uow
              )

              schedulingMessageBus.handle(
                CreateClientCommand(
                  id = Client.Id("Client123"),
                  name = HumanName(
                    family = Name.Family("Last"),
                    given = Name.Given("First"),
                    prefix = Name.Prefix(""),
                    suffix = Name.Suffix(""),
                    period = Period.Unknown
                  ),
                  gender = Gender.UNKNOWN,
                  contactPoints = setOf(
                    ContactPoint.Phone.Unverified("917-555-5555"),
                    ContactPoint.Email.Unverified("hello@world.com")
                  )
                ),
                uow
              )

              val start = Instant.now()
              val end = start.plus(1, ChronoUnit.HOURS)

              schedulingMessageBus.handle(
                CreateAppointmentCommand(
                  id = Appointment.Id("Appointment123"),
                  client = Client.Id("Client123"),
                  practitioner = Practitioner.Id("Practitioner123"),
                  practice = Practice.Id("Practice123"),
                  state = AppointmentState.SCHEDULED,
                  period = Period.Bounded(
                    start.toLocalDateTimeUTC(),
                    end.toLocalDateTimeUTC()
                  )
                ),
                uow
              )

              // When
              schedulingMessageBus.handle(
                MarkAppointmentAttendedCommand(
                  appointment = Appointment.Id("Appointment123")
                ),
                uow
              )

              // Then
              with(JooqSchedulingWebViews(it.dsl())) {
                findAppointmentOrThrow("Appointment123").shouldBe(
                  AppointmentRecord(
                    id = "Appointment123",
                    clientId = "Client123",
                    practiceId = "Practice123",
                    practitionerId = "Practitioner123",
                    state = "ATTENDED",
                    periodStart = start,
                    periodEnd = end,
                  )
                )
              }
            }
          }
        }
      }
    }

    context("MarkAppointmentUnattendedCommand") {
      should("result in updated AppointmentEntity") {
        testTransaction {
          val uow = SchedulingJooqUnitOfWork(it)

          runBlocking {
            uow.transaction {
              // Given
              schedulingMessageBus.handle(
                CreatePracticeCommand(
                  id = Practice.Id("Practice123"),
                  name = Practice.Name("Hello & Associates"),
                  owner = Practitioner.Id("Practitioner123"),
                  contactPoints = setOf(
                    ContactPoint.Phone.Unverified("917-555-5555"),
                    ContactPoint.Email.Unverified("hello@associates.com")
                  )
                ),
                uow
              )

              schedulingMessageBus.handle(
                CreatePractitionerCommand(
                  id = Practitioner.Id("Practitioner123"),
                  user = UserId("User123"),
                  name = HumanName(
                    given = Name.Given("First"),
                    family = Name.Family("Last"),
                    suffix = Name.Suffix(""),
                    prefix = Name.Prefix(""),
                    period = Period.Unknown
                  ),
                  gender = Gender.UNKNOWN,
                  contactPoints = emptySet()
                ),
                uow
              )

              schedulingMessageBus.handle(
                CreateClientCommand(
                  id = Client.Id("Client123"),
                  name = HumanName(
                    family = Name.Family("Last"),
                    given = Name.Given("First"),
                    prefix = Name.Prefix(""),
                    suffix = Name.Suffix(""),
                    period = Period.Unknown
                  ),
                  gender = Gender.UNKNOWN,
                  contactPoints = setOf(
                    ContactPoint.Phone.Unverified("917-555-5555"),
                    ContactPoint.Email.Unverified("hello@world.com")
                  )
                ),
                uow
              )

              val start = Instant.now()
              val end = start.plus(1, ChronoUnit.HOURS)

              schedulingMessageBus.handle(
                CreateAppointmentCommand(
                  id = Appointment.Id("Appointment123"),
                  client = Client.Id("Client123"),
                  practitioner = Practitioner.Id("Practitioner123"),
                  practice = Practice.Id("Practice123"),
                  state = AppointmentState.SCHEDULED,
                  period = Period.Bounded(
                    start.toLocalDateTimeUTC(),
                    end.toLocalDateTimeUTC()
                  )
                ),
                uow
              )

              // When
              schedulingMessageBus.handle(
                MarkAppointmentUnattendedCommand(
                  appointment = Appointment.Id("Appointment123")
                ),
                uow
              )

              // Then
              with(JooqSchedulingWebViews(it.dsl())) {
                findAppointmentOrThrow("Appointment123").shouldBe(
                  AppointmentRecord(
                    id = "Appointment123",
                    clientId = "Client123",
                    practiceId = "Practice123",
                    practitionerId = "Practitioner123",
                    state = "UNATTENDED",
                    periodStart = start,
                    periodEnd = end,
                  )
                )
              }
            }
          }
        }
      }
    }

    context("CancelAppointmentCommand") {
      should(" result in updated AppointmentEntity") {
        testTransaction {
          val uow = SchedulingJooqUnitOfWork(it)

          runBlocking {
            uow.transaction {
              // Given
              schedulingMessageBus.handle(
                CreatePracticeCommand(
                  id = Practice.Id("Practice123"),
                  name = Practice.Name("Hello & Associates"),
                  owner = Practitioner.Id("Practitioner123"),
                  contactPoints = setOf(
                    ContactPoint.Phone.Unverified("917-555-5555"),
                    ContactPoint.Email.Unverified("hello@associates.com")
                  )
                ),
                uow
              )

              schedulingMessageBus.handle(
                CreatePractitionerCommand(
                  id = Practitioner.Id("Practitioner123"),
                  user = UserId("User123"),
                  name = HumanName(
                    given = Name.Given("First"),
                    family = Name.Family("Last"),
                    suffix = Name.Suffix(""),
                    prefix = Name.Prefix(""),
                    period = Period.Unknown
                  ),
                  gender = Gender.UNKNOWN,
                  contactPoints = emptySet()
                ),
                uow
              )

              schedulingMessageBus.handle(
                CreateClientCommand(
                  id = Client.Id("Client123"),
                  name = HumanName(
                    family = Name.Family("Last"),
                    given = Name.Given("First"),
                    prefix = Name.Prefix(""),
                    suffix = Name.Suffix(""),
                    period = Period.Unknown
                  ),
                  gender = Gender.UNKNOWN,
                  contactPoints = setOf(
                    ContactPoint.Phone.Unverified("917-555-5555"),
                    ContactPoint.Email.Unverified("hello@world.com")
                  )
                ),
                uow
              )

              val start = Instant.now()
              val end = start.plus(1, ChronoUnit.HOURS)

              schedulingMessageBus.handle(
                CreateAppointmentCommand(
                  id = Appointment.Id("Appointment123"),
                  client = Client.Id("Client123"),
                  practitioner = Practitioner.Id("Practitioner123"),
                  practice = Practice.Id("Practice123"),
                  state = AppointmentState.SCHEDULED,
                  period = Period.Bounded(
                    start.toLocalDateTimeUTC(),
                    end.toLocalDateTimeUTC()
                  )
                ),
                uow
              )

              // When
              schedulingMessageBus.handle(
                CancelAppointmentCommand(
                  appointment = Appointment.Id("Appointment123")
                ),
                uow
              )

              // Then
              with(JooqSchedulingWebViews(it.dsl())) {
                findAppointmentOrThrow("Appointment123").shouldBe(
                  AppointmentRecord(
                    id = "Appointment123",
                    clientId = "Client123",
                    practiceId = "Practice123",
                    practitionerId = "Practitioner123",
                    state = "CANCELED",
                    periodStart = start,
                    periodEnd = end,
                  )
                )
              }
            }
          }
        }
      }
    }
  }
})
