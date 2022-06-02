package com.acme.web.server.scheduling.data

import com.acme.sql.web_server.tables.references.APPOINTMENTS
import com.acme.sql.web_server.tables.references.CLIENTS
import com.acme.sql.web_server.tables.references.CLIENT_CONTACT_POINTS
import com.acme.sql.web_server.tables.references.CLIENT_NAMES
import com.acme.sql.web_server.tables.references.PRACTICES
import com.acme.sql.web_server.tables.references.PRACTICE_CONTACT_POINTS
import com.acme.sql.web_server.tables.references.PRACTITIONERS
import com.acme.sql.web_server.tables.references.PRACTITIONER_CONTACT_POINTS
import com.acme.sql.web_server.tables.references.PRACTITIONER_NAMES
import org.jooq.Configuration
import org.jooq.DSLContext
import java.time.ZoneOffset

class JooqSchedulingWebViews(private val dsl: DSLContext) : SchedulingWebViews {

  constructor(configuration: Configuration) : this(configuration.dsl())

  override fun findPractice(id: String): PracticeRecord? =
    dsl.selectFrom(
      PRACTICES.leftJoin(PRACTICE_CONTACT_POINTS)
        .on(PRACTICE_CONTACT_POINTS.PRACTICE_ID.eq(PRACTICES.ID))
    )
      .where(PRACTICES.ID.eq(id))
      .fetch()
      .let { result ->
        if (result.isEmpty()) {
          null
        } else {
          // TODO: Convert to use new Jooq multiset feature
          val contactPoints = result.mapNotNull {
            it[PRACTICE_CONTACT_POINTS.SYSTEM]?.run {
              ContactPointRecord(
                system = it[PRACTICE_CONTACT_POINTS.SYSTEM]!!,
                value = it[PRACTICE_CONTACT_POINTS.VALUE]!!,
                verifiedAt = it[PRACTICE_CONTACT_POINTS.VERIFIED_AT]?.toInstant(ZoneOffset.UTC),
              )
            }
          }.toSet()

          result.first().let {
            PracticeRecord(
              id = it[PRACTICES.ID]!!,
              name = it[PRACTICES.NAME]!!,
              contactPoints = contactPoints
            )
          }
        }
      }

  override fun findClient(id: String): ClientRecord? =
    dsl.selectFrom(
      CLIENTS
        .leftJoin(CLIENT_CONTACT_POINTS)
        .on(CLIENT_CONTACT_POINTS.CLIENT_ID.eq(CLIENTS.ID))
        .leftJoin(CLIENT_NAMES)
        .on(CLIENT_NAMES.CLIENT_ID.eq(CLIENTS.ID))
    )
      .where(CLIENTS.ID.eq(id))
      .fetch()
      .let { result ->
        if (result.isEmpty()) null
        else {
          val names = result.mapNotNull {
            it[CLIENT_NAMES.FAMILY]?.run {
              HumanNameRecord(
                family = it[CLIENT_NAMES.FAMILY]!!,
                given = it[CLIENT_NAMES.GIVEN]!!,
                prefix = it[CLIENT_NAMES.PREFIX]!!,
                suffix = it[CLIENT_NAMES.SUFFIX]!!,
                periodStart = it[CLIENT_NAMES.PERIOD_START]?.toInstant(ZoneOffset.UTC),
                periodEnd = it[CLIENT_NAMES.PERIOD_END]?.toInstant(ZoneOffset.UTC)
              )
            }
          }.toSet()

          val contactPoints = result.mapNotNull {
            it[CLIENT_CONTACT_POINTS.SYSTEM]?.run {
              ContactPointRecord(
                system = it[CLIENT_CONTACT_POINTS.SYSTEM]!!,
                value = it[CLIENT_CONTACT_POINTS.VALUE]!!,
                verifiedAt = it[CLIENT_CONTACT_POINTS.VERIFIED_AT]?.toInstant(ZoneOffset.UTC),
              )
            }
          }.toSet()

          result.first().let {
            ClientRecord(
              id = it[CLIENTS.ID]!!,
              gender = it[CLIENTS.GENDER]!!,
              names = names,
              contactPoints = contactPoints
            )
          }
        }
      }

  override fun findPractitioner(id: String): PractitionerRecord? =
    dsl.selectFrom(
      PRACTITIONERS
        .leftJoin(PRACTITIONER_CONTACT_POINTS)
        .on(PRACTITIONER_CONTACT_POINTS.PRACTITIONER_ID.eq(PRACTITIONERS.ID))
        .leftJoin(PRACTITIONER_NAMES)
        .on(PRACTITIONER_NAMES.PRACTITIONER_ID.eq(PRACTITIONERS.ID))
    )
      .where(PRACTITIONERS.ID.eq(id))
      .fetch()
      .let { result ->
        if (result.isEmpty()) null
        else {
          val names = result.mapNotNull {
            it[PRACTITIONER_NAMES.FAMILY]?.run {
              HumanNameRecord(
                family = it[PRACTITIONER_NAMES.FAMILY]!!,
                given = it[PRACTITIONER_NAMES.GIVEN]!!,
                prefix = it[PRACTITIONER_NAMES.PREFIX]!!,
                suffix = it[PRACTITIONER_NAMES.SUFFIX]!!,
                periodStart = it[PRACTITIONER_NAMES.PERIOD_START]?.toInstant(ZoneOffset.UTC),
                periodEnd = it[PRACTITIONER_NAMES.PERIOD_END]?.toInstant(ZoneOffset.UTC)
              )
            }
          }.toSet()

          val contactPoints = result.mapNotNull {
            it[PRACTITIONER_CONTACT_POINTS.VALUE]?.run {
              ContactPointRecord(
                system = it[PRACTITIONER_CONTACT_POINTS.SYSTEM]!!,
                value = it[PRACTITIONER_CONTACT_POINTS.VALUE]!!,
                verifiedAt = it[PRACTITIONER_CONTACT_POINTS.VERIFIED_AT]?.toInstant(ZoneOffset.UTC),
              )
            }
          }.toSet()

          result.first().let {
            PractitionerRecord(
              id = it[PRACTITIONERS.ID]!!,
              gender = it[PRACTITIONERS.GENDER]!!,
              names = names,
              contactPoints = contactPoints
            )
          }
        }
      }

  override fun findAppointment(id: String): AppointmentRecord? =
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
      .fetchOne()?.let {
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
