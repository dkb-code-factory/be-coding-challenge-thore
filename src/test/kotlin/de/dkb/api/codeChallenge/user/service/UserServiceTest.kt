package de.dkb.api.codeChallenge.user.service

import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.port.LoadUserPort
import de.dkb.api.codeChallenge.user.service.port.SaveUserPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*

class UserServiceTest {
    private val saveUserPort: SaveUserPort = mockk()
    private val loadUserPort: LoadUserPort = mockk()
    private val subject: UserService = UserService(saveUserPort, loadUserPort)

    @Test
    fun `registerUser - given a user - when registering - then user is saved via port`() {
        // Given
        val user = User(
            id = UUID.randomUUID(),
            notifications = setOf(User.NotificationType.TYPE_1, User.NotificationType.TYPE_2)
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
        val notificationType = User.NotificationType.TYPE_1
        val message = "Test notification message"
        val user = User(
            id = userId,
            notifications = setOf(User.NotificationType.TYPE_1, User.NotificationType.TYPE_2)
        )
        every { loadUserPort.loadById(userId) } returns Optional.of(user)

        // Capture console output
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // When
        subject.sendNotification(userId, notificationType, message)

        // Then
        System.setOut(originalOut)
        val output = outputStream.toString()
        assert(output.contains("Sending notification of type $notificationType"))
        assert(output.contains("to user $userId"))
        assert(output.contains(message))
        verify(exactly = 1) { loadUserPort.loadById(userId) }
    }

    @Test
    fun `sendNotification - given user not subscribed to notification type - when sending notification - then notification is not sent`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = User.NotificationType.TYPE_3
        val message = "Test notification message"
        val user = User(
            id = userId,
            notifications = setOf(User.NotificationType.TYPE_1, User.NotificationType.TYPE_2)
        )
        every { loadUserPort.loadById(userId) } returns Optional.of(user)

        // Capture console output
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // When
        subject.sendNotification(userId, notificationType, message)

        // Then
        System.setOut(originalOut)
        val output = outputStream.toString()
        assert(output.isEmpty())
        verify(exactly = 1) { loadUserPort.loadById(userId) }
    }

    @Test
    fun `sendNotification - given user does not exist - when sending notification - then notification is not sent`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = User.NotificationType.TYPE_1
        val message = "Test notification message"
        every { loadUserPort.loadById(userId) } returns Optional.empty()

        // Capture console output
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        // When
        subject.sendNotification(userId, notificationType, message)

        // Then
        System.setOut(originalOut)
        val output = outputStream.toString()
        assert(output.isEmpty())
        verify(exactly = 1) { loadUserPort.loadById(userId) }
    }
}
