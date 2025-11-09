package de.dkb.api.codeChallenge.user.adapter.`in`.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

// These should in the best case be generated through openApi or smth. similar
data class NotifyUserRequest(
    @field:NotNull(message = "User ID cannot be null")
    val userId: UUID,
    
    @field:NotNull(message = "Notification type cannot be null")
    val notificationType: NotificationType,
    
    @field:NotBlank(message = "Message cannot be blank")
    val message: String
)
