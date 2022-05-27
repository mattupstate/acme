package com.acme.web.server.scheduling

import com.acme.scheduling.AppointmentAttendedEvent
import com.acme.scheduling.AppointmentCanceledEvent
import com.acme.scheduling.AppointmentCreatedEvent
import com.acme.scheduling.AppointmentUnattendedEvent
import com.acme.scheduling.ClientCreatedEvent
import com.acme.scheduling.PracticeCreatedEvent
import com.acme.scheduling.PractitionerCreatedEvent
import com.acme.scheduling.schedulingMessageBus as baseSchedulingMessageBus
import com.acme.web.server.scheduling.data.onAppointmentAttended
import com.acme.web.server.scheduling.data.onAppointmentCanceled
import com.acme.web.server.scheduling.data.onAppointmentCreated
import com.acme.web.server.scheduling.data.onAppointmentUnattended
import com.acme.web.server.scheduling.data.onClientCreated
import com.acme.web.server.scheduling.data.onPracticeCreated
import com.acme.web.server.scheduling.data.onPractitionerCreated

val schedulingMessageBus = baseSchedulingMessageBus.copy().apply {
  addEventHandler(
    PracticeCreatedEvent::class to onPracticeCreated,
    PractitionerCreatedEvent::class to onPractitionerCreated,
    ClientCreatedEvent::class to onClientCreated,
    AppointmentCreatedEvent::class to onAppointmentCreated,
    AppointmentAttendedEvent::class to onAppointmentAttended,
    AppointmentUnattendedEvent::class to onAppointmentUnattended,
    AppointmentCanceledEvent::class to onAppointmentCanceled,
  )
}
