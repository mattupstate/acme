package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.HasRevision
import com.acme.scheduling.Practice
import com.acme.sql.scheduling.Tables.PRACTICES
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.JSONB

class JooqPracticeAggregateRepository(
  private val dsl: DSLContext
) : AggregateRepository<Practice, Practice.Id> {

  constructor(configuration: Configuration) : this(configuration.dsl())

  override fun find(id: Practice.Id): Practice? =
    dsl.selectFrom(PRACTICES)
      .where(PRACTICES.ID.eq(id.value))
      .fetchOne {
        Json.decodeFromString<Practice>(it.aggregate.data())
      }

  override fun get(id: Practice.Id): Practice = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Practice.Id, block: () -> Throwable): Practice =
    find(id) ?: throw block()

  override fun exists(id: Practice.Id): Boolean =
    dsl.fetchExists(PRACTICES, PRACTICES.ID.eq(id.value))

  override fun save(aggregate: Practice) {
    val json = JSONB.valueOf(Json.encodeToString(aggregate))
    if (HasRevision.aggregateIsNew(aggregate)) {
      dsl.insertInto(
        PRACTICES,
        PRACTICES.ID,
        PRACTICES.VERSION_NUMBER,
        PRACTICES.AGGREGATE,
      ).values(
        aggregate.id.value,
        aggregate.revision,
        json
      ).execute()
    } else {
      dsl.update(PRACTICES)
        .set(PRACTICES.AGGREGATE, json)
        .set(PRACTICES.VERSION_NUMBER, aggregate.revision)
        .where(PRACTICES.ID.eq(aggregate.id.value))
        .and(PRACTICES.VERSION_NUMBER.eq(aggregate.revision - 1))
        .execute()
    }
  }
}
