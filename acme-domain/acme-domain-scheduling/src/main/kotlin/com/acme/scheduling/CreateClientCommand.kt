package com.acme.scheduling

import com.acme.core.Command
import com.acme.core.MetaData

data class CreateClientCommand(
  val id: Client.Id,
  val name: HumanName,
  val gender: Gender,
  val contactPoints: Set<ContactPoint>
) : Command {
  override val metadata: MetaData = MetaData()
}
