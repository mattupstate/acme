package com.acme.scheduling

import com.acme.core.AggregateRepository

interface SchedulingPersistenceModule {
  val appointments: AggregateRepository<Appointment, Appointment.Id>
  val practices: AggregateRepository<Practice, Practice.Id>
  val clients: AggregateRepository<Client, Client.Id>
  val practitioners: AggregateRepository<Practitioner, Practitioner.Id>
}
