package com.acme.web.api.scheduling.data

interface SchedulingWebViews {
  suspend fun findPractice(id: String): PracticeRecord?
  suspend fun findPracticeOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): PracticeRecord = findPractice(id) ?: throw block()

  suspend fun findClient(id: String): ClientRecord?
  suspend fun findClientOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): ClientRecord = findClient(id) ?: throw block()

  suspend fun findPractitioner(id: String): PractitionerRecord?
  suspend fun findPractitionerOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): PractitionerRecord = findPractitioner(id) ?: throw block()

  suspend fun findAppointment(id: String): AppointmentRecord?
  suspend fun findAppointmentOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): AppointmentRecord = findAppointment(id) ?: throw block()
}
