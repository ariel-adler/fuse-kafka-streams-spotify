package com.tikalfuse.spotify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpotifyApplication

fun main(args: Array<String>) {
    runApplication<SpotifyApplication>(*args)
}
