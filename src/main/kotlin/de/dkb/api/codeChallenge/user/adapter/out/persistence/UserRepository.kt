package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.port.LoadUserPort
import de.dkb.api.codeChallenge.user.service.port.SaveUserPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class UserRepository(
    private val jpaUserRepository: JpaUserRepository,
    private val jpaNotificationCategoryRepository: JpaNotificationCategoryRepository
) : SaveUserPort, LoadUserPort {
    
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun save(user: User): User {
        log.info("Saving user: id={}, categories={}", user.id, user.subscribedCategories)
        jpaUserRepository.save(user.toEntity())
        
        jpaNotificationCategoryRepository.deleteByUserId(user.id)
        
        val categoryEntities = user.toCategoryEntities()
        if (categoryEntities.isNotEmpty()) {
            jpaNotificationCategoryRepository.saveAll(categoryEntities)
        }
        
        log.info("User saved successfully: id={}", user.id)
        return user
    }

    override fun loadById(userId: UUID): User? {
        log.info("Loading user by id: {}", userId)
        return jpaUserRepository.findById(userId).map {
            val categories = jpaNotificationCategoryRepository.findByUserId(userId)
            it.toDomain(categories)
        }.orElse(null)
    }
}

