package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.NotificationCategoryEntity
import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import de.dkb.api.codeChallenge.user.domain.NotificationCategory
import de.dkb.api.codeChallenge.user.domain.User

fun UserEntity.toDomain(categories: List<NotificationCategoryEntity>): User =
    User(
        id = this.id,
        subscribedCategories = categories
            .map { NotificationCategory.valueOf(it.category) }
            .toSet()
    )

fun User.toEntity(): UserEntity =
    UserEntity(id = this.id)

fun User.toCategoryEntities(): List<NotificationCategoryEntity> =
    this.subscribedCategories.map { category ->
        NotificationCategoryEntity(
            userId = this.id,
            category = category.name
        )
    }

