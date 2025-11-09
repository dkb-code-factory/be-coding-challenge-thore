package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.NotificationCategoryEntity
import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import de.dkb.api.codeChallenge.user.domain.NotificationCategory
import de.dkb.api.codeChallenge.user.domain.User
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class UserRepositoryTest {
    private val jpaUserRepository: JpaUserRepository = mockk()
    private val jpaNotificationCategoryRepository: JpaNotificationCategoryRepository = mockk()
    private val subject: UserRepository = UserRepository(jpaUserRepository, jpaNotificationCategoryRepository)

    @Test
    fun `save - given a user - when saving - then user is persisted via JPA repository`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            subscribedCategories = setOf(NotificationCategory.CATEGORY_A)
        )
        val userEntity = UserEntity(id = userId)
        val categoryEntities = listOf(NotificationCategoryEntity(userId, "CATEGORY_A"))
        
        every { jpaUserRepository.save(any()) } returns userEntity
        justRun { jpaNotificationCategoryRepository.deleteByUserId(userId) }
        every { jpaNotificationCategoryRepository.saveAll(any<List<NotificationCategoryEntity>>()) } returns categoryEntities

        // When
        val result = subject.save(user)

        // Then
        assertEquals(user.id, result.id)
        assertEquals(1, result.subscribedCategories.size)
        assertTrue(result.subscribedCategories.contains(NotificationCategory.CATEGORY_A))
        verify(exactly = 1) { jpaUserRepository.save(any()) }
        verify(exactly = 1) { jpaNotificationCategoryRepository.deleteByUserId(userId) }
        verify(exactly = 1) { jpaNotificationCategoryRepository.saveAll(any<List<NotificationCategoryEntity>>()) }
    }

    @Test
    fun `findById - given existing user id - when finding user - then user is returned`() {
        // Given
        val userId = UUID.randomUUID()
        val userEntity = UserEntity(id = userId)
        val categoryEntities = listOf(NotificationCategoryEntity(userId, "CATEGORY_B"))
        
        every { jpaUserRepository.findById(userId) } returns Optional.of(userEntity)
        every { jpaNotificationCategoryRepository.findByUserId(userId) } returns categoryEntities

        // When
        val result = subject.loadById(userId)

        // Then
        assertTrue(result != null)
        assertEquals(userId, result!!.id)
        assertEquals(1, result.subscribedCategories.size)
        assertTrue(result.subscribedCategories.contains(NotificationCategory.CATEGORY_B))
        verify(exactly = 1) { jpaUserRepository.findById(userId) }
        verify(exactly = 1) { jpaNotificationCategoryRepository.findByUserId(userId) }
    }

    @Test
    fun `findById - given non-existing user id - when finding user - then empty optional is returned`() {
        // Given
        val userId = UUID.randomUUID()
        every { jpaUserRepository.findById(userId) } returns Optional.empty()

        // When
        val result = subject.loadById(userId)

        // Then
        assertTrue(result == null)
        verify(exactly = 1) { jpaUserRepository.findById(userId) }
        verify(exactly = 0) { jpaNotificationCategoryRepository.findByUserId(any()) }
    }

    @Test
    fun `save - given user with empty categories - when saving - then user is persisted correctly`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            subscribedCategories = emptySet()
        )
        val userEntity = UserEntity(id = userId)
        
        every { jpaUserRepository.save(any()) } returns userEntity
        justRun { jpaNotificationCategoryRepository.deleteByUserId(userId) }

        // When
        val result = subject.save(user)

        // Then
        assertEquals(user.id, result.id)
        assertTrue(result.subscribedCategories.isEmpty())
        verify(exactly = 1) { jpaUserRepository.save(any()) }
        verify(exactly = 1) { jpaNotificationCategoryRepository.deleteByUserId(userId) }
        verify(exactly = 0) { jpaNotificationCategoryRepository.saveAll(any<List<NotificationCategoryEntity>>()) }
    }
}
