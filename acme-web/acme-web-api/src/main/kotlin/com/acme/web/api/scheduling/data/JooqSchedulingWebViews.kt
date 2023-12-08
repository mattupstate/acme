package com.acme.web.api.scheduling.data

import com.acme.sql.web_server.tables.references.APPOINTMENTS
import com.acme.sql.web_server.tables.references.CLIENTS
import com.acme.sql.web_server.tables.references.CLIENT_CONTACT_POINTS
import com.acme.sql.web_server.tables.references.CLIENT_NAMES
import com.acme.sql.web_server.tables.references.PRACTICES
import com.acme.sql.web_server.tables.references.PRACTICE_CONTACT_POINTS
import com.acme.sql.web_server.tables.references.PRACTITIONERS
import com.acme.sql.web_server.tables.references.PRACTITIONER_CONTACT_POINTS
import com.acme.sql.web_server.tables.references.PRACTITIONER_NAMES
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.Records.mapping
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import java.time.LocalDateTime
import java.time.ZoneOffset

class JooqSchedulingWebViews(private val dsl: DSLContext) : SchedulingWebViews {

  private val newContactPointRecord = { system: String?, value: String?, verifiedAt: LocalDateTime? ->
    ContactPointRecord(
      system = system!!,
      value = value!!,
      verifiedAt = verifiedAt?.toInstant(ZoneOffset.UTC)
    )
  }

  private val newHumanNameRecord =
    { given: String?, family: String?, prefix: String?, suffix: String?, periodStart: LocalDateTime?, periodEnd: LocalDateTime? ->
      HumanNameRecord(
        given = given!!,
        family = family!!,
        prefix = prefix!!,
        suffix = suffix!!,
        periodStart = periodStart?.toInstant(ZoneOffset.UTC),
        periodEnd = periodEnd?.toInstant(ZoneOffset.UTC)
      )
    }

  constructor(configuration: Configuration) : this(configuration.dsl())

  override suspend fun findPractice(id: String): PracticeRecord? =
    dsl.select(
      PRACTICES.ID, PRACTICES.NAME,
      multiset(
        select(
          PRACTICE_CONTACT_POINTS.SYSTEM,
          PRACTICE_CONTACT_POINTS.VALUE,
          PRACTICE_CONTACT_POINTS.VERIFIED_AT
        )
          .from(PRACTICE_CONTACT_POINTS)
          .where(PRACTICE_CONTACT_POINTS.PRACTICE_ID.eq(PRACTICES.ID))
          .orderBy(
            PRACTICE_CONTACT_POINTS.SYSTEM,
            PRACTICE_CONTACT_POINTS.VALUE,
            PRACTICE_CONTACT_POINTS.VERIFIED_AT
          )
      )
        .`as`("contactPoints")
        .convertFrom { result ->
          result.map(mapping(newContactPointRecord))
        }
    )
      .from(PRACTICES)
      .where(PRACTICES.ID.eq(id))
      .awaitFirstOrNull()?.let {
        PracticeRecord(
          id = it[PRACTICES.ID]!!,
          name = it[PRACTICES.NAME]!!,
          contactPoints = (it["contactPoints"] as List<ContactPointRecord>)
        )
      }

  override suspend fun findClient(id: String): ClientRecord? =
    dsl.select(
      CLIENTS.ID, CLIENTS.GENDER,
      multiset(
        select(
          CLIENT_NAMES.GIVEN,
          CLIENT_NAMES.FAMILY,
          CLIENT_NAMES.PREFIX,
          CLIENT_NAMES.SUFFIX,
          CLIENT_NAMES.PERIOD_START,
          CLIENT_NAMES.PERIOD_END
        )
          .from(CLIENT_NAMES)
          .where(CLIENT_NAMES.CLIENT_ID.eq(CLIENTS.ID))
          .orderBy(
            CLIENT_NAMES.FAMILY,
            CLIENT_NAMES.PERIOD_START
          )
      ).`as`("names")
        .convertFrom { result ->
          result.map(mapping(newHumanNameRecord))
        },
      multiset(
        select(
          CLIENT_CONTACT_POINTS.SYSTEM,
          CLIENT_CONTACT_POINTS.VALUE,
          CLIENT_CONTACT_POINTS.VERIFIED_AT
        )
          .from(CLIENT_CONTACT_POINTS)
          .where(CLIENT_CONTACT_POINTS.CLIENT_ID.eq(CLIENTS.ID))
          .orderBy(
            CLIENT_CONTACT_POINTS.SYSTEM,
            CLIENT_CONTACT_POINTS.VALUE,
            CLIENT_CONTACT_POINTS.VERIFIED_AT
          )
      )
        .`as`("contactPoints")
        .convertFrom { result ->
          result.map(mapping(newContactPointRecord))
        }
    )
      .from(CLIENTS)
      .where(CLIENTS.ID.eq(id))
      .awaitFirstOrNull()?.let {
        ClientRecord(
          id = it[CLIENTS.ID]!!,
          gender = it[CLIENTS.GENDER]!!,
          names = (it["names"] as List<HumanNameRecord>),
          contactPoints = (it["contactPoints"] as List<ContactPointRecord>)
        )
      }

  override suspend fun findPractitioner(id: String): PractitionerRecord? =
    dsl.select(
      PRACTITIONERS.ID, PRACTITIONERS.GENDER,
      multiset(
        select(
          PRACTITIONER_CONTACT_POINTS.SYSTEM,
          PRACTITIONER_CONTACT_POINTS.VALUE,
          PRACTITIONER_CONTACT_POINTS.VERIFIED_AT
        )
          .from(PRACTITIONER_CONTACT_POINTS)
          .where(PRACTITIONER_CONTACT_POINTS.PRACTITIONER_ID.eq(PRACTITIONERS.ID))
          .orderBy(
            PRACTITIONER_CONTACT_POINTS.SYSTEM,
            PRACTITIONER_CONTACT_POINTS.VALUE,
            PRACTITIONER_CONTACT_POINTS.VERIFIED_AT
          )
      )
        .`as`("contactPoints")
        .convertFrom { result ->
          result.map(mapping(newContactPointRecord))
        },
      multiset(
        select(
          PRACTITIONER_NAMES.GIVEN,
          PRACTITIONER_NAMES.FAMILY,
          PRACTITIONER_NAMES.PREFIX,
          PRACTITIONER_NAMES.SUFFIX,
          PRACTITIONER_NAMES.PERIOD_START,
          PRACTITIONER_NAMES.PERIOD_END
        )
          .from(PRACTITIONER_NAMES)
          .where(PRACTITIONER_NAMES.PRACTITIONER_ID.eq(PRACTITIONERS.ID))
          .orderBy(
            PRACTITIONER_NAMES.FAMILY,
            PRACTITIONER_NAMES.PERIOD_START
          )
      ).`as`("names")
        .convertFrom { result ->
          result.map(mapping(newHumanNameRecord))
        }
    )
      .from(PRACTITIONERS)
      .where(PRACTITIONERS.ID.eq(id))
      .awaitFirstOrNull()?.let {
        PractitionerRecord(
          id = it[PRACTITIONERS.ID]!!,
          gender = it[PRACTITIONERS.GENDER]!!,
          names = it["names"] as List<HumanNameRecord>,
          contactPoints = (it["contactPoints"] as List<ContactPointRecord>)
        )
      }

  override suspend fun findAppointment(id: String): AppointmentRecord? =
    dsl.selectFrom(
      APPOINTMENTS
        .leftJoin(PRACTITIONERS)
        .on(PRACTITIONERS.ID.eq(APPOINTMENTS.PRACTITIONER_ID))
        .leftJoin(PRACTICES)
        .on(PRACTICES.ID.eq(APPOINTMENTS.PRACTICE_ID))
        .leftJoin(CLIENTS)
        .on(CLIENTS.ID.eq(APPOINTMENTS.CLIENT_ID))
    )
      .where(APPOINTMENTS.ID.eq(id))
      .awaitFirstOrNull()?.let {
        AppointmentRecord(
          id = it[APPOINTMENTS.ID]!!,
          practiceId = it[PRACTICES.ID]!!,
          practitionerId = it[PRACTITIONERS.ID]!!,
          clientId = it[CLIENTS.ID]!!,
          state = it[APPOINTMENTS.STATE]!!,
          periodStart = it[APPOINTMENTS.PERIOD_START]?.toInstant(ZoneOffset.UTC),
          periodEnd = it[APPOINTMENTS.PERIOD_END]?.toInstant(ZoneOffset.UTC)
        )
      }
}
