package com.acme.web.server.scheduling.data

import com.acme.scheduling.PractitionerCreatedEvent
import java.time.ZoneOffset

interface SchedulingWebViewService {
  fun insertPractitioner(event: PractitionerCreatedEvent)
}

class InMemorySchedulingWebViewService(
  private val practitioners: MutableMap<String, PractitionerRecord> = mutableMapOf()
) : SchedulingWebViewService {

  override fun insertPractitioner(event: PractitionerCreatedEvent) {
    val id = event.practitioner.id.value
    practitioners[id] = PractitionerRecord(
      id = id,
      names = event.practitioner.names.map {
        val (start, end) = it.period.getTimeValues()
        HumanNameRecord(
          given = it.given.value,
          family = it.family.value,
          suffix = it.suffix.value,
          prefix = it.prefix.value,
          periodStart = start?.toInstant(ZoneOffset.UTC),
          periodEnd = end?.toInstant(ZoneOffset.UTC)
        )
      },
      gender = event.practitioner.gender.name,
      contactPoints = event.practitioner.contactPoints.map {
        ContactPointRecord(
          value = it.value,
          system = it.toSystemString(),
          verifiedAt = it.getVerifiedAtValue()?.toInstant(ZoneOffset.UTC)
        )
      }
    )
  }
}
