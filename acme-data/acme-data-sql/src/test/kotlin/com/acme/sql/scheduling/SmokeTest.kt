package com.acme.sql.scheduling

import com.acme.jooq.asExcluded
import com.acme.sql.scheduling.tables.references.APPOINTMENTS
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.JSONB
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class SmokeTest : ShouldSpec({
  val clock = Clock.systemUTC()

  should("be able to insert and query against the appointments table") {
    testTransaction { dsl ->
      val now = LocalDateTime.ofInstant(Instant.now(clock), ZoneOffset.UTC)
      val json = JSONB.valueOf(
        """
        {
          "hello": "world"
        }
      """.trimIndent()
      )

      dsl.insertInto(
        APPOINTMENTS,
        APPOINTMENTS.ID,
        APPOINTMENTS.REVISION,
        APPOINTMENTS.AGGREGATE,
        APPOINTMENTS.CREATED_AT,
        APPOINTMENTS.UPDATED_AT
      ).values(
        "123",
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

      dsl.selectFrom(APPOINTMENTS)
        .where(APPOINTMENTS.ID.eq("123"))
        .awaitFirstOrNull().shouldNotBeNull()

      dsl.selectFrom(APPOINTMENTS)
        .where(APPOINTMENTS.ID.eq("345"))
        .awaitFirstOrNull().shouldBeNull()
    }
  }
})
