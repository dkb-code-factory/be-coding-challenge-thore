package de.dkb.api.codeChallenge.user.service

import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.port.LoadUserPort
import de.dkb.api.codeChallenge.user.service.port.SaveUserPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val saveUserPort: SaveUserPort,
    private val loadUserPort: LoadUserPort
) {

    fun registerUser(user: User): User = saveUserPort.save(user)

    fun sendNotification(userId: UUID, notificationType: User.NotificationType, message: String) {
        loadUserPort.loadById(userId)
            .filter { it.hasNotificationType(notificationType) }
            .ifPresent {
                // Logic to send notification to user
                println(
                    "Sending notification of type $notificationType" +
                            " to user ${it.id}: $message"
                )
            }
    }
}
