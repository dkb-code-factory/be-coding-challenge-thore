package de.dkb.api.codeChallenge.user.adapter.`in`.web.dto

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val notifications: Set<String>
)
