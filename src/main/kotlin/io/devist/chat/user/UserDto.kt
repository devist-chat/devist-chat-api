package io.devist.chat.user

import java.io.Serializable
import java.util.*

class UserDto(
        var id: UUID? = null,
        var name: String? = null,
        var password: String? = null,
        var email: String? = null,
        var status: UserStatus? = null,
        var emailVerificationHash: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDto

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}