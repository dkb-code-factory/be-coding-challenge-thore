package de.dkb.api.codeChallenge.user.adapter.`in`.api

import de.dkb.api.codeChallenge.user.domain.User

fun RegisterUserRequest.toUser(): User =
    User(
        id = this.id,
        notifications = this.notifications.toDomain()
    )

fun NotificationType.toDomain(): User.NotificationType =
    when (this) {
        NotificationType.TYPE_1 -> User.NotificationType.TYPE_1
        NotificationType.TYPE_2 -> User.NotificationType.TYPE_2
        NotificationType.TYPE_3 -> User.NotificationType.TYPE_3
        NotificationType.TYPE_4 -> User.NotificationType.TYPE_4
        NotificationType.TYPE_5 -> User.NotificationType.TYPE_5
    }

fun Set<NotificationType>.toDomain(): Set<User.NotificationType> =
    this.map { it.toDomain() }.toSet()
