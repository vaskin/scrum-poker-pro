package com.scrumpokerpro.websocket

import java.util.UUID

data class MeetingEvent(
    val eventType: MeetingEventType? = null,
    var userId: UUID? = null,
    var meetingId: UUID? = null,
    var issueId: UUID? = null,
    var payload: Map<String, Any>? = null
)
