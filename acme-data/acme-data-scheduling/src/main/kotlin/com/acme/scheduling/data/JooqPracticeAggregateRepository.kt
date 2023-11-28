package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.PersistedAggregate
import com.acme.core.PersistenceMetaData
import com.acme.jooq.asExcluded
import com.acme.scheduling.Practice
import com.acme.sql.scheduling.tables.Practices.Companion.PRACTICES
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.DSLContext
import org.jooq.JSONB
import java.time.Clock
import java.time.LocalDateTime

class JooqPracticeAggregateRepository(
  private val dsl: DSLContext,
  private val clock: Clock = Clock.systemUTC()
) : AggregateRepository<Practice, Practice.Id> {

  override fun find(id: Practice.Id): PersistedAggregate<Practice>? =
    dsl.selectFrom(PRACTICES)
      .where(PRACTICES.ID.eq(id.value))
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

  override fun get(id: Practice.Id): PersistedAggregate<Practice> = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Practice.Id, block: () -> Throwable): PersistedAggregate<Practice> =
    find(id) ?: throw block()

  override fun exists(id: Practice.Id): Boolean =
    dsl.fetchExists(PRACTICES, PRACTICES.ID.eq(id.value))

  override fun save(aggregate: Practice) {
    val now = LocalDateTime.now(clock)
    val json = JSONB.valueOf(Json.encodeToString(aggregate))

    dsl.insertInto(
      PRACTICES,
      PRACTICES.ID,
      PRACTICES.REVISION,
      PRACTICES.AGGREGATE,
      PRACTICES.CREATED_AT,
      PRACTICES.UPDATED_AT,
    ).values(
      aggregate.id.value,
      1,
      json,
      now,
      now
    )
      .onConflict(PRACTICES.ID)
      .doUpdate()
      .set(PRACTICES.AGGREGATE, PRACTICES.AGGREGATE.asExcluded())
      .set(PRACTICES.REVISION, PRACTICES.REVISION.add(1))
      .set(PRACTICES.UPDATED_AT, now)
      .execute()
  }
}
