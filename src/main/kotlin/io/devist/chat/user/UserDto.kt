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

}