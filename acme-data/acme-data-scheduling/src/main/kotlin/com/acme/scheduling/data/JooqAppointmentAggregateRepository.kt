package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.PersistedAggregate
import com.acme.core.PersistenceMetaData
import com.acme.scheduling.Appointment
import com.acme.sql.scheduling.tables.references.APPOINTMENTS
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.asExcluded
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class JooqAppointmentAggregateRepository(
  private val dsl: DSLContext,
  private val clock: Clock = Clock.systemUTC()
) : AggregateRepository<Appointment, Appointment.Id> {

  override fun find(id: Appointment.Id): PersistedAggregate<Appointment>? =
    dsl.selectFrom(APPOINTMENTS)
      .where(APPOINTMENTS.ID.eq(id.value))
      .fetchOne {
        PersistedAggregate(
          aggregate = Json.decodeFromString<Appointment>(it.aggregate!!.data()),
          metaData = PersistenceMetaData(
            createdAt = it.createdAt!!,
            updatedAt = it.updatedAt!!,
            revision = it.revision!!,
          )
        )
      }

  override fun get(id: Appointment.Id): PersistedAggregate<Appointment> = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Appointment.Id, block: () -> Throwable): PersistedAggregate<Appointment> =
    find(id) ?: throw block()

  override fun exists(id: Appointment.Id): Boolean =
    dsl.fetchExists(APPOINTMENTS, APPOINTMENTS.ID.eq(id.value))

  override fun save(aggregate: Appointment) {
    val now = LocalDateTime.ofInstant(Instant.now(clock), ZoneOffset.UTC)
    val json = JSONB.valueOf(Json.encodeToString(aggregate))

    dsl.insertInto(
      APPOINTMENTS,
      APPOINTMENTS.ID,
      APPOINTMENTS.REVISION,
      APPOINTMENTS.AGGREGATE,
      APPOINTMENTS.CREATED_AT,
      APPOINTMENTS.UPDATED_AT
    ).values(
      aggregate.id.value,
      1,
      json,
      now,
      now,
    )
      .onConflict(APPOINTMENTS.ID)
      .doUpdate()
      .set(APPOINTMENTS.AGGREGATE, APPOINTMENTS.AGGREGATE.asExcluded())
      .set(APPOINTMENTS.REVISION, APPOINTMENTS.REVISION.add(1))
      .set(APPOINTMENTS.UPDATED_AT, now)
      .execute()
  }
}
