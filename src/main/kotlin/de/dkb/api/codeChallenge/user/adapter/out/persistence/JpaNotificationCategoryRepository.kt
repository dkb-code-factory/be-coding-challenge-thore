package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.NotificationCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaNotificationCategoryRepository : JpaRepository<NotificationCategoryEntity, NotificationCategoryEntity.NotificationCategoryId> {
    fun findByUserId(userId: UUID): List<NotificationCategoryEntity>
    fun deleteByUserId(userId: UUID)
}
