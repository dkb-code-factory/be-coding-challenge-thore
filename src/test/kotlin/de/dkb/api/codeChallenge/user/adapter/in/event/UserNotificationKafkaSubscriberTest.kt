package de.dkb.api.codeChallenge.user.adapter.`in`.event

import de.dkb.api.codeChallenge.user.adapter.`in`.api.NotificationType
import de.dkb.api.codeChallenge.user.adapter.`in`.api.NotifyUserRequest
import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*

class UserNotificationKafkaSubscriberTest {
    private val userService: UserService = mockk(relaxed = true)
    private val subject: UserNotificationKafkaSubscriber = UserNotificationKafkaSubscriber(userService)

    @Test
    fun `consumeNotification - given a notification request - when consuming - then notification is sent via service`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = NotificationType.TYPE_1
        val message = "Test notification message"
        val request = NotifyUserRequest(
            userId = userId,
            notificationType = notificationType,
            message = message
        )
        every { userService.sendNotification(userId, User.NotificationType.TYPE_1, message) } returns Unit

        // When
        subject.consumeNotification(request)

        // Then
        verify(exactly = 1) { 
            userService.sendNotification(userId, User.NotificationType.TYPE_1, message) 
        }
    }

    @Test
    fun `consumeNotification - given notification with type2 - when consuming - then notification is sent with correct type`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = NotificationType.TYPE_2
        val message = "Another test message"
        val request = NotifyUserRequest(
            userId = userId,
            notificationType = notificationType,
            message = message
        )
        every { userService.sendNotification(userId, User.NotificationType.TYPE_2, message) } returns Unit

        // When
        subject.consumeNotification(request)

        // Then
        verify(exactly = 1) { 
            userService.sendNotification(userId, User.NotificationType.TYPE_2, message) 
        }
    }

    @Test
    fun `consumeNotification - given notification with type5 - when consuming - then notification is sent with correct type`() {
        // Given
        val userId = UUID.randomUUID()
        val notificationType = NotificationType.TYPE_5
        val message = "Type 5 notification"
        val request = NotifyUserRequest(
            userId = userId,
            notificationType = notificationType,
            message = message
        )
        every { userService.sendNotification(userId, User.NotificationType.TYPE_5, message) } returns Unit

        // When
        subject.consumeNotification(request)

        // Then
        verify(exactly = 1) { 
            userService.sendNotification(userId, User.NotificationType.TYPE_5, message) 
        }
    }
}
