package com.acme.scheduling

import an.awesome.pipelinr.Command

data class CreatePractitionerCommand2(
  val id: Practitioner.Id,
  val user: UserId,
  val name: HumanName,
  val gender: Gender,
  val contactPoints: Set<ContactPoint>
) : Command<Unit>
