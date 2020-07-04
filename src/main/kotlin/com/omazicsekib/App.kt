package com.omazicsekib

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent


class App(
        private val eventLogger: EventLogger = EventLogger()
) : RequestHandler<ScheduledEvent, String> {

    override fun handleRequest(event: ScheduledEvent, context: Context?): String {
        val response = "200 OK"

        eventLogger.logEvent(event, context)

        return response;
    }
}
