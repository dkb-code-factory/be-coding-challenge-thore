package de.dkb.api.codeChallenge.user.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import org.jetbrains.annotations.NotNull

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID,
    @field:NotNull
    @Convert(converter = NotificationStringSetConverter::class)
    var notifications: MutableSet<String> = mutableSetOf(),
) {
    // Default constructor for Hibernate
    constructor() : this(UUID.randomUUID(), mutableSetOf())
}
