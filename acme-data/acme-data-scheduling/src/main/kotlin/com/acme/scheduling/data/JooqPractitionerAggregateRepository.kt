package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.PersistedAggregate
import com.acme.core.PersistenceMetaData
import com.acme.scheduling.Practitioner
import com.acme.sql.scheduling.tables.Practitioners.Companion.PRACTITIONERS
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.DSLContext
import org.jooq.JSONB
import org.jooq.impl.asExcluded
import java.time.Clock
import java.time.LocalDateTime

class JooqPractitionerAggregateRepository(
  private val dsl: DSLContext,
  private val clock: Clock = Clock.systemUTC()
) : AggregateRepository<Practitioner, Practitioner.Id> {

  override fun find(id: Practitioner.Id): PersistedAggregate<Practitioner>? =
    dsl.selectFrom(PRACTITIONERS)
      .where(PRACTITIONERS.ID.eq(id.value))
      .fetchOne {
        PersistedAggregate(
          aggregate = Json.decodeFromString(it.aggregate!!.data()),
          metaData = PersistenceMetaData(
            createdAt = it.createdAt!!,
            updatedAt = it.updatedAt!!,
            revision = it.revision!!,
          )
        )
      }

  override fun get(id: Practitioner.Id): PersistedAggregate<Practitioner> = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Practitioner.Id, block: () -> Throwable): PersistedAggregate<Practitioner> =
    find(id) ?: throw block()

  override fun exists(id: Practitioner.Id): Boolean =
    dsl.fetchExists(PRACTITIONERS, PRACTITIONERS.ID.eq(id.value))

  override fun save(aggregate: Practitioner) {
    val now = LocalDateTime.now(clock)
    val json = JSONB.valueOf(Json.encodeToString(aggregate))

    dsl.insertInto(
      PRACTITIONERS,
      PRACTITIONERS.ID,
      PRACTITIONERS.REVISION,
      PRACTITIONERS.AGGREGATE,
      PRACTITIONERS.CREATED_AT,
      PRACTITIONERS.UPDATED_AT
    ).values(
      aggregate.id.value,
      1,
      json,
      now,
      now
    )
      .onConflict(PRACTITIONERS.ID)
      .doUpdate()
      .set(PRACTITIONERS.AGGREGATE, PRACTITIONERS.AGGREGATE.asExcluded())
      .set(PRACTITIONERS.REVISION, PRACTITIONERS.REVISION.add(1))
      .set(PRACTITIONERS.UPDATED_AT, now)
      .execute()
  }
}
