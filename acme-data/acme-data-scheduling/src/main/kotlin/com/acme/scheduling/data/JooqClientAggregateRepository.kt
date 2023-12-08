package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.core.PersistedAggregate
import com.acme.core.PersistenceMetaData
import com.acme.jooq.asExcluded
import com.acme.scheduling.Client
import com.acme.sql.scheduling.tables.references.CLIENTS
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jooq.DSLContext
import org.jooq.JSONB
import java.time.Clock
import java.time.LocalDateTime

class JooqClientAggregateRepository(
  private val dsl: DSLContext,
  private val clock: Clock = Clock.systemUTC()
) : AggregateRepository<Client, Client.Id> {

  override suspend fun find(id: Client.Id): PersistedAggregate<Client>? =
    dsl.selectFrom(CLIENTS)
      .where(CLIENTS.ID.eq(id.value))
      .awaitFirstOrNull()?.let {
        PersistedAggregate(
          aggregate = Json.decodeFromString(it.aggregate!!.data()),
          metaData = PersistenceMetaData(
            createdAt = it.createdAt!!,
            updatedAt = it.updatedAt!!,
            revision = it.revision!!,
          )
        )
      }

  override suspend fun get(id: Client.Id): PersistedAggregate<Client> = getOrThrow(id) { NoSuchElementException() }

  override suspend fun getOrThrow(id: Client.Id, block: () -> Throwable): PersistedAggregate<Client> =
    find(id) ?: throw block()

  override suspend fun exists(id: Client.Id): Boolean =
    dsl.selectOne().from(CLIENTS).where(CLIENTS.ID.eq(id.value)).awaitFirstOrNull() != null

  override suspend fun save(aggregate: Client) {
    val now = LocalDateTime.now(clock)
    val json = JSONB.valueOf(Json.encodeToString(aggregate))

    dsl.insertInto(
      CLIENTS,
      CLIENTS.ID,
      CLIENTS.REVISION,
      CLIENTS.AGGREGATE,
      CLIENTS.CREATED_AT,
      CLIENTS.UPDATED_AT
    ).values(
      aggregate.id.value,
      1,
      json,
      now,
      now
    )
      .onConflict(CLIENTS.ID)
      .doUpdate()
      .set(CLIENTS.AGGREGATE, CLIENTS.AGGREGATE.asExcluded())
      .set(CLIENTS.REVISION, CLIENTS.REVISION.add(1))
      .set(CLIENTS.UPDATED_AT, now)
      .returning()
      .awaitFirst()
  }
}
