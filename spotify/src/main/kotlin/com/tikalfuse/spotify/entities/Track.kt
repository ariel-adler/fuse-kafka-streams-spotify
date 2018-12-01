package com.tikalfuse.spotify.entities

import com.fasterxml.jackson.annotation.JsonProperty


data class Track(@JsonProperty("id") val id: Long,
                 @JsonProperty("composer", required = false) val composer: String,
                 @JsonProperty("length", required = false) val length: Int,
                 @JsonProperty("album") val album: String,
                 @JsonProperty("performer") val performer: String,
                 @JsonProperty("genre") val genre: Genre)