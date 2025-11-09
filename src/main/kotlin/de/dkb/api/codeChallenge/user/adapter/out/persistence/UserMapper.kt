package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import de.dkb.api.codeChallenge.user.domain.User

fun User.toEntity(): UserEntity =
    UserEntity(
        id = this.id,
        notifications = this.notifications.mapTo(mutableSetOf()) { 
            // Convert "TYPE_1" -> "type1", "TYPE_2" -> "type2", etc.
            it.name.lowercase().replace("_", "")
        }
    )

fun UserEntity.toDomain(): User =
    User(
        id = this.id,
        notifications = this.notifications.mapTo(mutableSetOf()) {
            // Convert "type1" -> "TYPE_1", "type2" -> "TYPE_2", etc.
            val enumName = it.uppercase().replaceFirst("TYPE", "TYPE_")
            User.NotificationType.valueOf(enumName)
        }
    )
