package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.port.LoadUserPort
import de.dkb.api.codeChallenge.user.service.port.SaveUserPort
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class UserRepository(
    private val jpaUserRepository: JpaUserRepository
) : SaveUserPort, LoadUserPort {

    override fun save(user: User): User =
        jpaUserRepository.save(user.toEntity()).toDomain()

    override fun loadById(userId: UUID) = jpaUserRepository.findById(userId).map { it.toDomain() }
    
}
