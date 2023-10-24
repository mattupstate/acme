package com.acme.web.api.scheduling.data

interface SchedulingWebViews {
  fun findPractice(id: String): PracticeRecord?
  fun findPracticeOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): PracticeRecord = findPractice(id) ?: throw block()

  fun findClient(id: String): ClientRecord?
  fun findClientOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): ClientRecord = findClient(id) ?: throw block()

  fun findPractitioner(id: String): PractitionerRecord?
  fun findPractitionerOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): PractitionerRecord = findPractitioner(id) ?: throw block()

  fun findAppointment(id: String): AppointmentRecord?
  fun findAppointmentOrThrow(
    id: String,
    block: () -> Throwable = ::NoSuchElementException
  ): AppointmentRecord = findAppointment(id) ?: throw block()
}
