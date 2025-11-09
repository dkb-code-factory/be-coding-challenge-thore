package de.dkb.api.codeChallenge.user.adapter.`in`.api

import com.fasterxml.jackson.annotation.JsonProperty

enum class NotificationType {
    @JsonProperty("type1") TYPE_1,
    @JsonProperty("type2") TYPE_2,
    @JsonProperty("type3") TYPE_3,
    @JsonProperty("type4") TYPE_4,
    @JsonProperty("type5") TYPE_5,
}
