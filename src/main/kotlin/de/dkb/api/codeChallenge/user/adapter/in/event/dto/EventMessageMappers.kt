package de.dkb.api.codeChallenge.user.adapter.`in`.event.dto

import de.dkb.api.codeChallenge.user.domain.NotificationType as DomainNotificationType

fun NotificationType.toDomain(): DomainNotificationType =
    when (this) {
        NotificationType.TYPE_1 -> DomainNotificationType.TYPE_1
        NotificationType.TYPE_2 -> DomainNotificationType.TYPE_2
        NotificationType.TYPE_3 -> DomainNotificationType.TYPE_3
        NotificationType.TYPE_4 -> DomainNotificationType.TYPE_4
        NotificationType.TYPE_5 -> DomainNotificationType.TYPE_5
        NotificationType.TYPE_6 -> DomainNotificationType.TYPE_6
    }
