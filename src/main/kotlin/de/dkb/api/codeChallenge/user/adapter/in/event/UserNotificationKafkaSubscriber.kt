package de.dkb.api.codeChallenge.user.adapter.`in`.event

import de.dkb.api.codeChallenge.user.adapter.`in`.event.dto.UserNotificationMessage
import de.dkb.api.codeChallenge.user.adapter.`in`.event.dto.toDomain
import de.dkb.api.codeChallenge.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserNotificationKafkaSubscriber(private val userService: UserService) {
    
    private val log = LoggerFactory.getLogger(javaClass)

    // For the challenge this is deactivated, but consider this functionality of a heavy use in the real life system
    @KafkaListener(
        topics = ["notifications"],
        groupId = "codechallenge_group",
        autoStartup = "\${kafka.listener.enabled:false}"
    )
    fun consumeNotification(message: UserNotificationMessage) {
        val result = userService.sendNotification(
            userId = message.userId,
            notificationType = message.notificationType.toDomain(),
            message = message.message
        )
        
        result.onLeft { error ->
            when (error) {
                is UserService.Error.UserNotFound ->
                    log.error("Failed to send notification - User not found: userId={}", error.userId)
                is UserService.Error.UserNotSubscribedToType ->
                    log.warn("User not subscribed to notification type: userId={}, type={}", 
                        error.userId, error.notificationType)
            }
        }
    }
}
