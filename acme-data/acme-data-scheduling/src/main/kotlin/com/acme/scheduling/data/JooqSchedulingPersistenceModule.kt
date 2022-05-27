package com.acme.scheduling.data

import com.acme.core.AggregateRepository
import com.acme.scheduling.Appointment
import com.acme.scheduling.Client
import com.acme.scheduling.Practice
import com.acme.scheduling.Practitioner
import com.acme.scheduling.SchedulingPersistenceModule
import org.jooq.DSLContext

class JooqSchedulingPersistenceModule(dsl: DSLContext) : SchedulingPersistenceModule {

  override val appointments: AggregateRepository<Appointment, Appointment.Id> =
    JooqAppointmentAggregateRepository(dsl)

  override val practices: AggregateRepository<Practice, Practice.Id> =
    JooqPracticeAggregateRepository(dsl)

  override val clients: AggregateRepository<Client, Client.Id> =
    JooqClientAggregateRepository(dsl)

  override val practitioners: AggregateRepository<Practitioner, Practitioner.Id> =
    JooqPractitionerAggregateRepository(dsl)
}
