package com.tikalfuse.spotify.controllers

import com.tikalfuse.spotify.entities.Track
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tracks")
class TracksController @Autowired constructor(val kafTemplate: KafkaTemplate<String, Track>, @Value("\${topic.tracks}") val tracksTopic: String) {

    @PostMapping
    fun addTrack(@RequestBody track: Track): HttpStatus {
        val fut :ListenableFuture<SendResult<String, Track>> = kafTemplate.send(tracksTopic, track.id.toString(), track)
        val success = !fut.completable().isCompletedExceptionally
        return if (success) HttpStatus.CREATED else HttpStatus.BAD_REQUEST
    }
}