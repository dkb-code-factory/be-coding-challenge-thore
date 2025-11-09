package de.dkb.api.codeChallenge.user.adapter.`in`.web

import de.dkb.api.codeChallenge.user.adapter.`in`.api.NotifyUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.api.RegisterUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.api.toDomain
import de.dkb.api.codeChallenge.user.adapter.`in`.api.toUser
import de.dkb.api.codeChallenge.user.domain.User
import de.dkb.api.codeChallenge.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@RequestBody request: RegisterUserRequest) =
        userService.registerUser(request.toUser())

    @PostMapping("/notify")
    fun sendNotification(@RequestBody request: NotifyUserRequest) =
        userService.sendNotification(
            userId = request.userId,
            notificationType = request.notificationType.toDomain(),
            message = request.message
        )
    
}
