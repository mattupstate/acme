package com.acme.scheduling.data

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
data class TimeFixture(
  val now: LocalDateTime,
  val clock: Clock
)
fun timeFixtureFactory(): TimeFixture {
  val now = Instant.ofEpochMilli(Instant.now().toEpochMilli())
  return TimeFixture(
    LocalDateTime.ofInstant(now, ZoneOffset.UTC),
    Clock.fixed(now, ZoneOffset.UTC)
  )
}
