package com.tikalfuse.spotify.controllers

import com.tikalfuse.spotify.entities.Genre
import com.tikalfuse.spotify.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController @Autowired constructor(val kafTemplate: KafkaTemplate<String, User>, @Value("\${topic.user-profile}") val userTopic: String) {

    @PostMapping
    fun addUser(@RequestBody user: User): HttpStatus {
        val fut :ListenableFuture<SendResult<String, User>> = kafTemplate.send(userTopic, user.email, user)
        val success = !fut.completable().isCompletedExceptionally
        return if (success) HttpStatus.CREATED else HttpStatus.BAD_REQUEST
    }
}