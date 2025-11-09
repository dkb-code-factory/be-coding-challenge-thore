package de.dkb.api.codeChallenge.user.domain

import java.util.UUID

data class User(
    val id: UUID,
    val notifications: Set<NotificationType>
) {
    fun hasNotificationType(notificationType: NotificationType): Boolean =
        notifications.contains(notificationType)

    enum class NotificationType {
        TYPE_1,
        TYPE_2,
        TYPE_3,
        TYPE_4,
        TYPE_5,
    }        
}
