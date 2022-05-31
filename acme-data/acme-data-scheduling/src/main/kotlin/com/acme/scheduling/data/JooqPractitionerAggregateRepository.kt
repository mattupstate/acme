package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.HasRevision
import com.acme.scheduling.Practitioner
import com.acme.sql.scheduling.Tables.PRACTITIONERS
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.JSONB

class JooqPractitionerAggregateRepository(
  private val dsl: DSLContext
) : AggregateRepository<Practitioner, Practitioner.Id> {

  constructor(configuration: Configuration) : this(configuration.dsl())

  override fun find(id: Practitioner.Id): Practitioner? =
    dsl.selectFrom(PRACTITIONERS)
      .where(PRACTITIONERS.ID.eq(id.value))
      .fetchOne {
        Json.decodeFromString<Practitioner>(it.aggregate.data())
      }

  override fun get(id: Practitioner.Id): Practitioner = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Practitioner.Id, block: () -> Throwable): Practitioner =
    find(id) ?: throw block()

  override fun exists(id: Practitioner.Id): Boolean =
    dsl.fetchExists(PRACTITIONERS, PRACTITIONERS.ID.eq(id.value))

  override fun save(aggregate: Practitioner) {
    val json = JSONB.valueOf(Json.encodeToString(aggregate))
    if (HasRevision.aggregateIsNew(aggregate)) {
      dsl.insertInto(
        PRACTITIONERS,
        PRACTITIONERS.ID,
        PRACTITIONERS.VERSION_NUMBER,
        PRACTITIONERS.AGGREGATE
      ).values(
        aggregate.id.value,
        aggregate.revision,
        json
      ).execute()
    } else {
      dsl.update(PRACTITIONERS)
        .set(PRACTITIONERS.AGGREGATE, json)
        .set(PRACTITIONERS.VERSION_NUMBER, aggregate.revision)
        .where(PRACTITIONERS.ID.eq(aggregate.id.value))
        .and(PRACTITIONERS.VERSION_NUMBER.eq(aggregate.revision - 1))
        .execute()
    }
  }
}
