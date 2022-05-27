@file:OptIn(KtorExperimentalLocationsAPI::class)

package com.acme.web.server.scheduling.ktor

import io.ktor.server.locations.KtorExperimentalLocationsAPI
import io.ktor.server.locations.Location

interface ObjectResourceLocation {
  val id: String
}

@Location("")
class RootResourceLocation

@Location("/practices")
class PracticeCollectionResourceLocation

@Location("/practices/{id}")
data class PracticeResourceLocation(override val id: String) : ObjectResourceLocation

@Location("/clients")
class ClientCollectionResourceLocation

@Location("/clients/{id}")
data class ClientResourceLocation(override val id: String) : ObjectResourceLocation

@Location("/practitioners")
class PractitionerCollectionResourceLocation

@Location("/practitioners/{id}")
data class PractitionerResourceLocation(override val id: String) : ObjectResourceLocation

@Location("/appointments")
class AppointmentCollectionResourceLocation

@Location("/appointments/{id}")
data class AppointmentResourceLocation(override val id: String) : ObjectResourceLocation {
  @Location("/attended")
  data class Attended(val parent: AppointmentResourceLocation)
  @Location("/unattended")
  data class Unattended(val parent: AppointmentResourceLocation)
  @Location("/cancel")
  data class Cancel(val parent: AppointmentResourceLocation)
}
