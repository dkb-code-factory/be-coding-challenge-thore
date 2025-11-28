package de.dkb.api.codeChallenge.user.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import de.dkb.api.codeChallenge.user.domain.NotificationType
import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.port.LoadUserPort
import de.dkb.api.codeChallenge.user.service.port.SaveUserPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val saveUserPort: SaveUserPort,
    private val loadUserPort: LoadUserPort
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun registerUser(user: User): User {
        log.info("Registering user: id={}, categories={}", user.id, user.subscribedCategories)
        return saveUserPort.save(user).also {
            log.info("User registered successfully: id={}", it.id)
        }
    }

    fun sendNotification(userId: UUID, notificationType: NotificationType, message: String): Either<Error, Unit> = either {
        log.info("Processing notification: userId={}, type={}", userId, notificationType)
        
        val user = ensureNotNull(loadUserPort.loadById(userId)) { 
            log.warn("User not found: userId={}", userId)
            Error.UserNotFound(userId)
        }
        ensure(user.isSubscribedToType(notificationType)) {
            log.warn("User not subscribed to notification type: userId={}, type={}", user.id, notificationType)
            Error.UserNotSubscribedToType(user.id, notificationType)
        }
        
        // Logic to send notification to user
        println(
            "Sending notification of type $notificationType" +
                    " to user ${user.id}: $message"
        )
        log.info("Notification sent: userId={}, type={}", user.id, notificationType)
        log.debug("Notification message content: {}", message)
    }

    sealed interface Error {
        data class UserNotFound(val userId: UUID) : Error
        data class UserNotSubscribedToType(val userId: UUID, val notificationType: NotificationType) : Error
    }    
}
