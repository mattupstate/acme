package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.PersistedAggregate
import com.acme.core.PersistenceMetaData
import com.acme.jooq.asExcluded
import com.acme.scheduling.Appointment
import com.acme.sql.scheduling.tables.references.APPOINTMENTS
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.DSLContext
import org.jooq.JSONB
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class JooqAppointmentAggregateRepository(
  private val dsl: DSLContext,
  private val clock: Clock = Clock.systemUTC()
) : AggregateRepository<Appointment, Appointment.Id> {

  override suspend fun findById(id: Appointment.Id): Result<PersistedAggregate<Appointment>> =
    runCatching {
      dsl.selectFrom(APPOINTMENTS)
        .where(APPOINTMENTS.ID.eq(id.value))
        .awaitFirst().let {
          PersistedAggregate(
            aggregate = Json.decodeFromString(it.aggregate.data()),
            metaData = PersistenceMetaData(
              createdAt = it.createdAt,
              updatedAt = it.updatedAt,
              revision = it.revision,
            )
          )
        }
    }

  override suspend fun exists(id: Appointment.Id): Boolean =
    dsl.selectOne().from(APPOINTMENTS).where(APPOINTMENTS.ID.eq(id.value)).awaitFirstOrNull() != null

  override suspend fun save(aggregate: Appointment) {
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
      .returning()
      .awaitFirst()
  }
}
