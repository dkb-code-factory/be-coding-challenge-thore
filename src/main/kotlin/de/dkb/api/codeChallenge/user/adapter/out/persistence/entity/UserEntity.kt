package de.dkb.api.codeChallenge.user.adapter.out.persistence.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID
) {
    constructor() : this(UUID.randomUUID())
}
