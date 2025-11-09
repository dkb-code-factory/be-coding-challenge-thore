package de.dkb.api.codeChallenge.user.adapter.`in`.api

import java.util.UUID

// Theses should in the best case be generated though openApi ore smth. similar
data class NotifyUserRequest(
    val userId: UUID,
    val notificationType: NotificationType,
    val message: String
)
