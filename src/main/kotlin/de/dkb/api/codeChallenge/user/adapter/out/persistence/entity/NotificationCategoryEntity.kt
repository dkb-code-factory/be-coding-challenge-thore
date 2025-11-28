package de.dkb.api.codeChallenge.user.adapter.out.persistence.entity

import jakarta.persistence.*
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "notification_category")
@IdClass(NotificationCategoryEntity.NotificationCategoryId::class)
data class NotificationCategoryEntity(
    @Id
    @Column(name = "user_id", columnDefinition = "uuid")
    val userId: UUID,
    
    @Id
    @Column(name = "category", length = 50)
    val category: String
) {
    constructor() : this(UUID.randomUUID(), "")

    data class NotificationCategoryId(
        val userId: UUID = UUID.randomUUID(),
        val category: String = ""
    ) : Serializable
}
