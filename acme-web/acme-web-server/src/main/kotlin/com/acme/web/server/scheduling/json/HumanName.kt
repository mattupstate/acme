package com.acme.web.server.scheduling.json

import kotlinx.serialization.Serializable

@Serializable
data class HumanName(
  val given: String,
  val family: String,
  val suffix: String,
  val prefix: String,
  val period: Period
) : Comparable<HumanName> {
  override fun compareTo(other: HumanName): Int =
    when {
      other == this -> 0
      family == other.family && given == other.given && prefix == other.prefix && suffix == other.suffix -> period.compareTo(other.period)
      family == other.family -> given.compareTo(other.given)
      else -> family.compareTo(other.family)
    }
}
