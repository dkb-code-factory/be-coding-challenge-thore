package de.dkb.api.codeChallenge.user.service.port

import de.dkb.api.codeChallenge.user.domain.User

interface SaveUserPort {
    fun save(user: User): User
}
