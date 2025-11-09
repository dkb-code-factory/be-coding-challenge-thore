package de.dkb.api.codeChallenge.user.domain

import java.util.UUID

data class User(
    val id: UUID,
    val subscribedCategories: Set<NotificationCategory>
) {
    fun isSubscribedToType(type: NotificationType): Boolean =
        subscribedCategories.any { category -> category.containsType(type) }
}
