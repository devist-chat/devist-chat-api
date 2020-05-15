package io.devist.chat.user

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(schema = "devist", name = "user")
class User(
        @Id
        @GeneratedValue
        var id: UUID? = null,

        @NotEmpty(message = "{javax.validations.user.name.notBlank}")
        @Size(min = 5, max = 1000, message = "{javax.validations.user.name.size}")
        var name: String,

        @NotEmpty(message = "{javax.validations.user.email.notBlank}")
        @Email(message = "{javax.validations.user.email.invalid}")
        var email: String,

        @NotNull
        var status: UserStatus,

        @NotNull
        var passwordHash: String,

        @NotNull
        var emailVerificationHash: String
) {
}