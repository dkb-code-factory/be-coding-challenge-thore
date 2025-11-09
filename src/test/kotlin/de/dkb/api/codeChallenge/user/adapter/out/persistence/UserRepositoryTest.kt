package de.dkb.api.codeChallenge.user.adapter.out.persistence

import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import de.dkb.api.codeChallenge.user.domain.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class UserRepositoryTest {
    private val jpaUserRepository: JpaUserRepository = mockk()
    private val subject: UserRepository = UserRepository(jpaUserRepository)

    @Test
    fun `save - given a user - when saving - then user is persisted via JPA repository`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            notifications = setOf(User.NotificationType.TYPE_1, User.NotificationType.TYPE_2)
        )
        val userEntity = UserEntity(
            id = userId,
            notifications = mutableSetOf("type1", "type2")
        )
        every { jpaUserRepository.save(any()) } returns userEntity

        // When
        val result = subject.save(user)

        // Then
        assertEquals(user.id, result.id)
        assertEquals(2, result.notifications.size)
        assertTrue(result.notifications.contains(User.NotificationType.TYPE_1))
        assertTrue(result.notifications.contains(User.NotificationType.TYPE_2))
        verify(exactly = 1) { jpaUserRepository.save(any()) }
    }

    @Test
    fun `findById - given existing user id - when finding user - then user is returned`() {
        // Given
        val userId = UUID.randomUUID()
        val userEntity = UserEntity(
            id = userId,
            notifications = mutableSetOf("type3", "type4")
        )
        every { jpaUserRepository.findById(userId) } returns Optional.of(userEntity)

        // When
        val result = subject.loadById(userId)

        // Then
        assertTrue(result.isPresent)
        assertEquals(userId, result.get().id)
        assertEquals(2, result.get().notifications.size)
        verify(exactly = 1) { jpaUserRepository.findById(userId) }
    }

    @Test
    fun `findById - given non-existing user id - when finding user - then empty optional is returned`() {
        // Given
        val userId = UUID.randomUUID()
        every { jpaUserRepository.findById(userId) } returns Optional.empty()

        // When
        val result = subject.loadById(userId)

        // Then
        assertTrue(result.isEmpty)
        verify(exactly = 1) { jpaUserRepository.findById(userId) }
    }

    @Test
    fun `save - given user with empty notifications - when saving - then user is persisted correctly`() {
        // Given
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            notifications = emptySet()
        )
        val userEntity = UserEntity(
            id = userId,
            notifications = mutableSetOf()
        )
        every { jpaUserRepository.save(any()) } returns userEntity

        // When
        val result = subject.save(user)

        // Then
        assertEquals(user.id, result.id)
        assertTrue(result.notifications.isEmpty())
        verify(exactly = 1) { jpaUserRepository.save(any()) }
    }
}
