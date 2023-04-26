package com.scrumpokerpro.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumpokerpro.service.participant.ParticipantService
import com.scrumpokerpro.utils.logger
import com.scrumpokerpro.websocket.MeetingEventType.JOIN
import com.scrumpokerpro.websocket.MeetingEventType.LEAVE
import kotlinx.coroutines.reactor.mono
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class MeetingHandler(
    val objectMapper: ObjectMapper,
    val jwtDecoderByJwkKeySetUri: JwtDecoder,
    val participantService: ParticipantService,
) : WebSocketHandler {

    val log by logger()

    val meetings: ConcurrentHashMap<String, Sinks.Many<MeetingEvent>> = ConcurrentHashMap()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val meetingId = session.handshakeInfo.uri.path.substringAfterLast("/")
        val params = session.handshakeInfo.uri.query.split("&").map { it.split("=") }.associate { it[0] to it[1] }
        val jwt: Jwt = jwtDecoderByJwkKeySetUri.decode(params["access_token"])
        val input = session
            .receive()
            .doOnSubscribe {
                doOnSubscribe(meetingId, jwt.getClaimAsString("name"), jwt.subject, JOIN)
            }.doOnNext {
                objectMapper.readValue(it.payloadAsText, MeetingEvent::class.java).apply {
                    this.userId = UUID.fromString(jwt.subject)
                    this.meetingId = UUID.fromString(meetingId)
                }.also {
                    getSink(meetingId).tryEmitNext(it)
                }
            }.doOnTerminate {
                doOnTerminate(meetingId, jwt.subject, LEAVE, "disconnected")
            }.doOnError {
                doOnTerminate(meetingId, jwt.subject, LEAVE, "error")
            }.doOnCancel {
                doOnTerminate(meetingId, jwt.subject, LEAVE, "canceled")
            }.then()

        val output = session.send(
            getSink(meetingId).asFlux().map { meetingEvent ->
                session.textMessage(objectMapper.writeValueAsString(meetingEvent))
            }
        ).then()

        return Mono.zip(input, output).then()
    }

    fun getSink(meetingId: String): Sinks.Many<MeetingEvent> {
        return meetings.computeIfAbsent(meetingId) { Sinks.many().multicast().directBestEffort() }
    }

    fun doOnTerminate(meetingId: String, userId: String, eventType: MeetingEventType, action: String) {
        mono {
            participantService.deleteParticipant(meetingId = UUID.fromString(meetingId), userId = UUID.fromString(userId))
        }.subscribe()
        val sink = getSink(meetingId)
        log.info("Websocket subscriber {}: meetingId = {}, userId = {}, total = {}", action, meetingId, userId, sink.currentSubscriberCount())
        sink.tryEmitNext(
            MeetingEvent(
                eventType = eventType,
                userId = UUID.fromString(userId),
                meetingId = UUID.fromString(meetingId)
            )
        )
    }

    fun doOnSubscribe(meetingId: String, userName: String, userId: String, eventType: MeetingEventType) {
        mono {
            participantService.createParticipant(meetingId = UUID.fromString(meetingId), name = userName, userId = UUID.fromString(userId))
        }.subscribe()
        val sink = getSink(meetingId)
        log.info("Websocket subscriber connected: meetingId = {}, userId = {}, total = {}", meetingId, userId, sink.currentSubscriberCount() + 1)
        sink.tryEmitNext(
            MeetingEvent(
                eventType = eventType,
                userId = UUID.fromString(userId),
                meetingId = UUID.fromString(meetingId)
            )
        )
    }
}
