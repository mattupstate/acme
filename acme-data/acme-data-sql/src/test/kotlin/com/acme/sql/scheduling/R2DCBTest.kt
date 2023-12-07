package com.acme.sql.scheduling

import com.acme.sql.scheduling.tables.references.APPOINTMENTS
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import kotlinx.coroutines.reactive.awaitFirstOrNull

class R2DCBTest : ShouldSpec({

  val db = listener(TestDatabaseListener())

  should("not block or hang?") {
    db.dsl.selectFrom(APPOINTMENTS)
      .where(APPOINTMENTS.ID.eq("123"))
      .awaitFirstOrNull().shouldBeNull()
  }
})
