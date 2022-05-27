package com.acme.scheduling

import com.acme.core.Command
import com.acme.core.MetaData

data class CreatePracticeCommand(
  val id: Practice.Id,
  val owner: Practitioner.Id,
  val name: Practice.Name,
  val contactPoints: Set<ContactPoint>
) : Command {
  override val metadata: MetaData = MetaData()
}
