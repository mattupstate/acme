package com.acme.web.api.scheduling

import com.acme.scheduling.AppointmentAttendedEvent
import com.acme.scheduling.AppointmentCanceledEvent
import com.acme.scheduling.AppointmentCreatedEvent
import com.acme.scheduling.AppointmentUnattendedEvent
import com.acme.scheduling.ClientCreatedEvent
import com.acme.scheduling.PracticeCreatedEvent
import com.acme.scheduling.PractitionerCreatedEvent
import com.acme.scheduling.schedulingMessageBus

val webApiSchedulingMessageBus = schedulingMessageBus.copy().apply {
  addEventHandler(
    PracticeCreatedEvent::class to ::onPracticeCreated,
    PractitionerCreatedEvent::class to ::onPractitionerCreated,
    ClientCreatedEvent::class to ::onClientCreated,
    AppointmentCreatedEvent::class to ::onAppointmentCreated,
    AppointmentAttendedEvent::class to ::onAppointmentAttended,
    AppointmentUnattendedEvent::class to ::onAppointmentUnattended,
    AppointmentCanceledEvent::class to ::onAppointmentCanceled,
  )
}
