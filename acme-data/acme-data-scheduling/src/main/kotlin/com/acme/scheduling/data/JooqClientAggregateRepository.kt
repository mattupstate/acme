package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.HasRevision
import com.acme.scheduling.Client
import com.acme.sql.scheduling.Tables.CLIENTS
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.JSONB

class JooqClientAggregateRepository(
  private val dsl: DSLContext
) : AggregateRepository<Client, Client.Id> {

  constructor(configuration: Configuration) : this(configuration.dsl())

  override fun find(id: Client.Id): Client? =
    dsl.selectFrom(CLIENTS)
      .where(CLIENTS.ID.eq(id.value))
      .fetchOne {
        Json.decodeFromString<Client>(it.aggregate.data())
      }

  override fun get(id: Client.Id): Client = getOrThrow(id) { NoSuchElementException() }

  override fun getOrThrow(id: Client.Id, block: () -> Throwable): Client =
    find(id) ?: throw block()

  override fun exists(id: Client.Id): Boolean =
    dsl.fetchExists(CLIENTS, CLIENTS.ID.eq(id.value))

  override fun save(aggregate: Client) {
    val json = JSONB.valueOf(Json.encodeToString(aggregate))
    if (HasRevision.aggregateIsNew(aggregate)) {
      dsl.insertInto(
        CLIENTS,
        CLIENTS.ID,
        CLIENTS.VERSION_NUMBER,
        CLIENTS.AGGREGATE
      ).values(
        aggregate.id.value,
        aggregate.revision,
        json
      ).execute()
    } else {
      dsl.update(CLIENTS)
        .set(CLIENTS.AGGREGATE, json)
        .set(CLIENTS.VERSION_NUMBER, aggregate.revision)
        .where(CLIENTS.ID.eq(aggregate.id.value))
        .and(CLIENTS.VERSION_NUMBER.eq(aggregate.revision - 1))
        .execute()
    }
  }
}
