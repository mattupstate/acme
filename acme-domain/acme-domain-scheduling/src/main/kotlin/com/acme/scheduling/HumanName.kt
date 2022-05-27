package com.acme.scheduling

import kotlinx.serialization.Serializable

@Serializable
data class HumanName(
  val family: Name.Family,
  val given: Name.Given,
  val prefix: Name.Prefix,
  val suffix: Name.Suffix,
  val period: Period
)
