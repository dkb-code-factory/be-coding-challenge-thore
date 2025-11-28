package de.dkb.api.codeChallenge.user.service

import de.dkb.api.codeChallenge.user.domain.NotificationCategory
import de.dkb.api.codeChallenge.user.domain.NotificationType
import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.port.LoadUserPort
import de.dkb.api.codeChallenge.user.service.port.SaveUserPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.UUID

class UserServiceTest {
    private val saveUserPort: SaveUserPort = mockk()
    private val loadUserPort: LoadUserPort = mockk()
    private val subject: UserService = UserService(saveUserPort, loadUserPort)

    @Test
    fun `registerUser - given a user - when registering - then user is saved via port`() {
        // Given
        val user = User(
            id = UUID.randomUUID(),
            subscribedCategories = setOf(NotificationCategory.CATEGORY_A)
        )
        every { saveUserPort.save(user) } returns user

        // When
        val result = subject.registerUser(user)

        // Then
        assertEquals(user, result)
        verify(exactly = 1) { saveUserPort.save(user) }
    }

    // This test is just for demonstration purposes, usually the actual logic would be tested here.
    @Test
    fun `sendNotification - given user subscribed to notification type - when sending notification - then notification is sent`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = NotificationType.TYPE_1
        val message = "Test notification message"
        val user = User(
            id = userId,
            subscribedCategories = setOf(NotificationCategory.CATEGORY_A)
        )
        every { loadUserPort.loadById(userId) } returns user

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // When
        val result = subject.sendNotification(userId, notificationType, message)

        // Then
        System.setOut(originalOut)
        assertTrue(result.isRight())
        val output = outputStream.toString()
        assert(output.contains("Sending notification of type $notificationType"))
        assert(output.contains("to user $userId"))
        assert(output.contains(message))
        verify(exactly = 1) { loadUserPort.loadById(userId) }
    }

    @Test
    fun `sendNotification - given user not subscribed to notification type - when sending notification - then returns UserNotSubscribedToType error`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = NotificationType.TYPE_4
        val message = "Test notification message"
        val user = User(
            id = userId,
            subscribedCategories = setOf(NotificationCategory.CATEGORY_A)
        )
        every { loadUserPort.loadById(userId) } returns user

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // When
        val result = subject.sendNotification(userId, notificationType, message)

        // Then
        System.setOut(originalOut)
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is UserService.Error.UserNotSubscribedToType)
            assertEquals(userId, (error as UserService.Error.UserNotSubscribedToType).userId)
            assertEquals(notificationType, error.notificationType)
        }
        val output = outputStream.toString()
        // Should not contain the "Sending notification" message since user is not subscribed
        assert(!output.contains("Sending notification of type $notificationType"))
        verify(exactly = 1) { loadUserPort.loadById(userId) }
    }

    @Test
    fun `sendNotification - given user does not exist - when sending notification - then returns UserNotFound error`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = NotificationType.TYPE_1
        val message = "Test notification message"
        every { loadUserPort.loadById(userId) } returns null

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // When
        val result = subject.sendNotification(userId, notificationType, message)

        // Then
        System.setOut(originalOut)
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is UserService.Error.UserNotFound)
            assertEquals(userId, (error as UserService.Error.UserNotFound).userId)
        }
        val output = outputStream.toString()
        // Should not contain the "Sending notification" message since user doesn't exist
        assert(!output.contains("Sending notification"))
        verify(exactly = 1) { loadUserPort.loadById(userId) }
    }
}
