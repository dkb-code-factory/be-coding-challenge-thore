package de.dkb.api.codeChallenge.user.adapter.`in`.event.dto

import com.fasterxml.jackson.annotation.JsonProperty

// Event-specific enum for Kafka message deserialization (just for demonstration)
enum class NotificationType {
    @JsonProperty("type1") TYPE_1,
    @JsonProperty("type2") TYPE_2,
    @JsonProperty("type3") TYPE_3,
    @JsonProperty("type4") TYPE_4,
    @JsonProperty("type5") TYPE_5,
    @JsonProperty("type6") TYPE_6,
}
