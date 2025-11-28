package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<UserEntity, UUID>
