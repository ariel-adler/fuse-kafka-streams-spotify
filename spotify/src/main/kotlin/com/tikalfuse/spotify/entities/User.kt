package com.tikalfuse.spotify.entities

data class User constructor(val email: String, val name: String, val age:Int, var genres: Array<String>) {

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