package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.HasRevision
import com.acme.scheduling.Appointment
import com.acme.sql.scheduling.Tables.APPOINTMENTS
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.DSLContext
import org.jooq.JSONB

class JooqAppointmentAggregateRepository(
  private val dsl: DSLContext,
) : AggregateRepository<Appointment, Appointment.Id> {

  override fun find(id: Appointment.Id): Appointment? =
    dsl.selectFrom(APPOINTMENTS)
      .where(APPOINTMENTS.ID.eq(id.value))
      .fetchOne {
        Json.decodeFromString<Appointment>(it.aggregate.data())
      }

  override fun get(id: Appointment.Id): Appointment = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Appointment.Id, block: () -> Throwable): Appointment =
    find(id) ?: throw block()

  override fun exists(id: Appointment.Id): Boolean =
    dsl.fetchExists(APPOINTMENTS, APPOINTMENTS.ID.eq(id.value))

  override fun save(aggregate: Appointment) {
    val json = JSONB.valueOf(Json.encodeToString(aggregate))
    if (HasRevision.aggregateIsNew(aggregate)) {
      dsl.insertInto(
        APPOINTMENTS,
        APPOINTMENTS.ID,
        APPOINTMENTS.VERSION_NUMBER,
        APPOINTMENTS.AGGREGATE
      ).values(
        aggregate.id.value,
        aggregate.revision,
        json
      ).execute()
    } else {
      dsl.update(APPOINTMENTS)
        .set(APPOINTMENTS.AGGREGATE, json)
        .set(APPOINTMENTS.VERSION_NUMBER, aggregate.revision)
        .where(APPOINTMENTS.ID.eq(aggregate.id.value))
        .and(APPOINTMENTS.VERSION_NUMBER.eq(aggregate.revision - 1))
        .execute()
    }
  }
}
