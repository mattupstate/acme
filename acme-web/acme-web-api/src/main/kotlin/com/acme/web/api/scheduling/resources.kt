package com.acme.web.api.scheduling

import com.acme.web.api.scheduling.data.AppointmentRecord
import com.acme.web.api.scheduling.data.ClientRecord
import com.acme.web.api.scheduling.data.PracticeRecord
import com.acme.web.api.scheduling.data.PractitionerRecord
import com.acme.web.api.scheduling.json.AppointmentResource
import com.acme.web.api.scheduling.json.AppointmentState
import com.acme.web.api.scheduling.json.ClientResource
import com.acme.web.api.scheduling.json.ContactPoint
import com.acme.web.api.scheduling.json.ContactPointSystem
import com.acme.web.api.scheduling.json.Gender
import com.acme.web.api.scheduling.json.HumanName
import com.acme.web.api.scheduling.json.Period
import com.acme.web.api.scheduling.json.PracticeResource
import com.acme.web.api.scheduling.json.PractitionerResource

fun AppointmentRecord.toResource(linkProvider: () -> AppointmentResource.Links) =
  AppointmentResource(
    links = linkProvider(),
    id = id,
    state = AppointmentState.valueOf(state),
    period = Period(
      periodStart,
      periodEnd
    )
  )

fun PractitionerRecord.toResource(linkProvider: () -> PractitionerResource.Links) =
  PractitionerResource(
    links = linkProvider(),
    id = id,
    names = names.map {
      HumanName(
        given = it.given,
        family = it.family,
        prefix = it.prefix,
        suffix = it.suffix,
        period = Period(
          it.periodStart,
          it.periodEnd
        )
      )
    }.sorted(),
    gender = Gender.valueOf(gender),
    contactPoints = contactPoints.map {
      ContactPoint(
        system = ContactPointSystem.valueOf(it.system),
        value = it.value,
        verifiedAt = it.verifiedAt
      )
    }.sorted(),
  )

fun ClientRecord.toResource(linkProvider: () -> ClientResource.Links) =
  ClientResource(
    links = linkProvider(),
    id = id,
    gender = Gender.valueOf(gender),
    names = names.map {
      HumanName(
        given = it.given,
        family = it.family,
        prefix = it.prefix,
        suffix = it.suffix,
        period = Period(
          it.periodStart,
          it.periodEnd
        )
      )
    }.sorted(),
    contactPoints = contactPoints.map {
      ContactPoint(
        system = ContactPointSystem.valueOf(it.system),
        value = it.value,
        verifiedAt = it.verifiedAt
      )
    }.sorted(),
  )

fun PracticeRecord.toResource(linkProvider: () -> PracticeResource.Links) =
  PracticeResource(
    links = linkProvider(),
    id = id,
    name = name,
    contactPoints = contactPoints.map {
      ContactPoint(
        system = ContactPointSystem.valueOf(it.system),
        value = it.value,
        verifiedAt = it.verifiedAt
      )
    }.sorted(),
  )
