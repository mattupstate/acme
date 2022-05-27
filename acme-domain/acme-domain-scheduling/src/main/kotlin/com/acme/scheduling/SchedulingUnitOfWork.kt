package com.acme.scheduling

import com.acme.core.UnitOfWork

interface SchedulingUnitOfWork : UnitOfWork {
  val repositories: SchedulingPersistenceModule
}
