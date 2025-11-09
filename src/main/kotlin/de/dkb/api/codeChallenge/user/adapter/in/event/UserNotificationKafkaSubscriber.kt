package de.dkb.api.codeChallenge.user.adapter.`in`.event

import de.dkb.api.codeChallenge.user.adapter.`in`.api.NotifyUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.api.toDomain
import de.dkb.api.codeChallenge.user.service.UserService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserNotificationKafkaSubscriber(private val userService: UserService) {

    // For the challenge this is deactivated, but consider this functionality of a heavy use in the real life system
    @KafkaListener(
        topics = ["notifications"],
        groupId = "codechallenge_group",
        autoStartup = "\${kafka.listener.enabled:false}"
    )
    fun consumeNotification(request: NotifyUserRequest) {
        userService.sendNotification(
            userId = request.userId,
            notificationType = request.notificationType.toDomain(),
            message = request.message
        )
    }
}
