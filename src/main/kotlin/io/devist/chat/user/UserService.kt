package io.devist.chat.user

import io.devist.chat.user.exceptions.UserActivationException
import io.devist.chat.user.exceptions.UserCreateException
import io.devist.chat.user.exceptions.UserDeactivationException
import io.devist.chat.user.exceptions.UserNotFoundException
import io.devist.chat.utils.messages.MessageService
import io.devist.chat.utils.validators.Validators
import io.devist.chat.utils.validators.isEmailValid
import io.devist.chat.utils.validators.isFullNameValid
import io.devist.chat.utils.validators.isValid
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
        val repository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val messages: MessageService,
        val validators: Validators) {

    val userNotFoundMessageById = "exception.UserNotFoundException.byId"
    val userNotFoundMessageByHash = "exception.UserNotFoundException.byHash"

    operator fun get(id: UUID?): UserDto {
        id?.let {
            val user = repository.findById(it).orElseThrow { UserNotFoundException(messages[userNotFoundMessageById, it]) }
            return UserDto(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    status = user.status
            )
        }
        throw UserNotFoundException(messages[userNotFoundMessageById, "?"])
    }

    operator fun get(hash: String?): UserDto {
        hash?.let {
            val user = repository.findByEmailVerificationHash(it).orElseThrow { UserNotFoundException(messages[userNotFoundMessageByHash, it]) }
            return UserDto(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    status = user.status
            )
        }
        throw UserNotFoundException(messages[userNotFoundMessageByHash, "?"])
    }

    fun create(user: UserDto): UserDto {
        validateCreate(user)

        val saved = repository.save(User(
                name = user.name!!,
                email = user.email!!,
                status = UserStatus.NEW,
                passwordHash = passwordEncoder.encode(user.password),
                emailVerificationHash = UUID.randomUUID().toString().toLowerCase()))

        return UserDto(id = saved.id,
                name = saved.name,
                email = saved.email,
                status = saved.status,
                emailVerificationHash = saved.emailVerificationHash)
    }

    fun activate(hash: String?) {
        validateHash(hash)

        hash?.let {
            val user = repository.findByEmailVerificationHash(it).orElseThrow { UserNotFoundException(messages[userNotFoundMessageByHash, it]) }

            if (user.status != UserStatus.NEW) throw  UserActivationException(messages["exceptions.UserActivateException.status.invalid", user.status])

            user.status = UserStatus.ACTIVE

            repository.save(user)
        }
    }

    fun deactivate(id: UUID?) {
        if (!validators.isValid(id)) throw UserDeactivationException(messages["exceptions.UserDeactivationException.id.empty"])

        id?.let {
            val user = this.repository.findById(it).orElseThrow { UserNotFoundException(messages["exceptions.UserNotFoundException.byId", it]) }

            if (user.status != UserStatus.NEW) throw  UserDeactivationException(messages["exceptions.UserDeactivationException.status.invalid", user.status])

            user.status = UserStatus.INACTIVE

            repository.save(user)
        }
    }


    fun validateHash(hash: String?) {
        if (!validators.isValid(hash)) throw UserActivationException(messages["exceptions.UserActivateException.hash.empty"])
    }

    fun validateCreate(user: UserDto) {
        validateUserName(user)
        validateUserEmail(user)
        validateUserPassword(user)
    }

    fun validateUserName(user: UserDto) {
        if (!validators.isValid(user.name)) throw UserCreateException(messages["exceptions.UserCreateException.name.empty"])
        if (!validators.isFullNameValid(user.name)) throw UserCreateException(messages["exceptions.UserCreateException.name.firstAndLastName"])
    }

    fun validateUserEmail(user: UserDto) {
        if (!validators.isEmailValid(user.email)) throw UserCreateException(messages["exceptions.UserCreateException.email.invalid"])
    }

    fun validateUserPassword(user: UserDto) {
        if (!validators.isValid(user.password, 5)) throw UserCreateException(messages["exceptions.UserCreateException.password.empty"])
    }
}