package de.dkb.api.codeChallenge.user.adapter.`in`.web.dto

import de.dkb.api.codeChallenge.user.domain.NotificationCategory
import de.dkb.api.codeChallenge.user.domain.NotificationType as DomainNotificationType
import de.dkb.api.codeChallenge.user.domain.User

fun RegisterUserRequest.toDomain(): User =
    User(
        id = this.id,
        subscribedCategories = NotificationCategory.fromNotificationTypes(
            this.notifications.toDomain()
        )
    )

fun User.toResponse(): UserResponse =
    UserResponse(
        id = this.id,
        notifications = this.subscribedCategories
            .flatMap { it.types }
            .map { it.name.lowercase().replace("_", "") }
            .toSet()
    )

fun NotificationType.toDomain(): DomainNotificationType =
    when (this) {
        NotificationType.TYPE_1 -> DomainNotificationType.TYPE_1
        NotificationType.TYPE_2 -> DomainNotificationType.TYPE_2
        NotificationType.TYPE_3 -> DomainNotificationType.TYPE_3
        NotificationType.TYPE_4 -> DomainNotificationType.TYPE_4
        NotificationType.TYPE_5 -> DomainNotificationType.TYPE_5
        NotificationType.TYPE_6 -> DomainNotificationType.TYPE_6
    }

fun Set<NotificationType>.toDomain(): Set<DomainNotificationType> =
    this.map { it.toDomain() }.toSet()
