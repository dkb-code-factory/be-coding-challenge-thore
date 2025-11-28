package de.dkb.api.codeChallenge.user.adapter.`in`.web

import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.NotifyUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.RegisterUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.UserResponse
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.toDomain
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.toResponse
import de.dkb.api.codeChallenge.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@Valid @RequestBody request: RegisterUserRequest): UserResponse =
        userService.registerUser(request.toDomain()).toResponse()
    

    @PostMapping("/notify")
    fun sendNotification(@Valid @RequestBody request: NotifyUserRequest): ResponseEntity<*> {
        return userService.sendNotification(
            userId = request.userId,
            notificationType = request.notificationType.toDomain(),
            message = request.message
        ).fold(
            ifLeft = { error -> error.toResponse() },
            ifRight = { ResponseEntity.noContent().build<Unit>() }
        )
    }
    
}

private fun UserService.Error.toResponse(): ResponseEntity<String> {
    return when (this) {
        is UserService.Error.UserNotFound -> 
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found: $userId")
        is UserService.Error.UserNotSubscribedToType -> 
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("User $userId is not subscribed to notification type $notificationType")
    }
}
