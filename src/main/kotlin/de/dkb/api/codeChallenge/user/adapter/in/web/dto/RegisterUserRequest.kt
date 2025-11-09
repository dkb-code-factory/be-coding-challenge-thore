package de.dkb.api.codeChallenge.user.adapter.`in`.web.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

// These should in the best case be generated through openApi or smth. similar
data class RegisterUserRequest(
    @field:NotNull(message = "User ID cannot be null")
    val id: UUID,
    
    @field:NotEmpty(message = "Notification types cannot be empty")
    val notifications: Set<NotificationType>
)
