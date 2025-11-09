package de.dkb.api.codeChallenge.user.service.port

import de.dkb.api.codeChallenge.user.domain.User
import java.util.Optional
import java.util.UUID

interface LoadUserPort {
    fun loadById(userId: UUID): Optional<User>
}
