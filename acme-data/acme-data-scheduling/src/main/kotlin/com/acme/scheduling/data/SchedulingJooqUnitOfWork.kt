package com.acme.scheduling.data

import com.acme.scheduling.SchedulingPersistenceModule
import com.acme.scheduling.SchedulingUnitOfWork
import com.acme.sql.AbstractJooqUnitOfWork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jooq.Configuration

open class SchedulingJooqUnitOfWork(config: Configuration, dispatcher: CoroutineDispatcher = Dispatchers.IO) :
  AbstractJooqUnitOfWork(config, dispatcher), SchedulingUnitOfWork {

  override val repositories: SchedulingPersistenceModule by lazy {
    JooqSchedulingPersistenceModule(dsl)
  }
}
