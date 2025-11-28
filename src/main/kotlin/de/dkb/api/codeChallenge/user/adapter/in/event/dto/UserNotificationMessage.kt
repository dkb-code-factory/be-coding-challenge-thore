package de.dkb.api.codeChallenge.user.adapter.`in`.event.dto

import java.util.UUID

// Kafka message DTO - for demonstration purposes
data class UserNotificationMessage(
    val userId: UUID,
    val notificationType: NotificationType,
    val message: String
)
