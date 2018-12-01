package com.tikalfuse.spotify.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class User (@JsonProperty("email") val email: String,
                 @JsonProperty("name") val name: String,
                 @JsonProperty("age") val age:Int,
                 @JsonProperty("genres") var genres: Array<Genre>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (email != other.email) return false
        if (name != other.name) return false
        if (age != other.age) return false
        if (!genres.contentEquals(other.genres)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + age
        result = 31 * result + genres.contentHashCode()
        return result
    }
}