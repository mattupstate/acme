package com.acme.scheduling.data

import com.acme.scheduling.SchedulingPersistenceModule
import com.acme.scheduling.SchedulingUnitOfWork
import com.acme.sql.AbstractJooqUnitOfWork
import org.jooq.Configuration

open class SchedulingJooqUnitOfWork(config: Configuration) :
  AbstractJooqUnitOfWork(config), SchedulingUnitOfWork {

  override val repositories: SchedulingPersistenceModule by lazy {
    JooqSchedulingPersistenceModule(dsl)
  }
}
