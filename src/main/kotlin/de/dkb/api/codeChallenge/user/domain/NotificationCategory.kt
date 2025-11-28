package de.dkb.api.codeChallenge.user.domain

enum class NotificationCategory(val types: Set<NotificationType>) {
    CATEGORY_A(setOf(NotificationType.TYPE_1, NotificationType.TYPE_2, NotificationType.TYPE_3, NotificationType.TYPE_6)),
    CATEGORY_B(setOf(NotificationType.TYPE_4, NotificationType.TYPE_5));

    fun containsType(type: NotificationType): Boolean = types.contains(type)

    companion object {
        fun fromNotificationType(type: NotificationType): NotificationCategory =
            entries.first { category -> category.containsType(type) }

        fun fromNotificationTypes(types: Set<NotificationType>): Set<NotificationCategory> =
            types.map { fromNotificationType(it) }.toSet()
    }
}
