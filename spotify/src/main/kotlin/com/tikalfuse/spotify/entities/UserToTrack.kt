package com.tikalfuse.spotify.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class UserToTrack (@JsonProperty("userId") val userId: String,
                        @JsonProperty("trackId") val trackId: String)