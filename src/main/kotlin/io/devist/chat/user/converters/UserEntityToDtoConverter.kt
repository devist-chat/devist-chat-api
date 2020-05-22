package io.devist.chat.user.converters

import io.devist.chat.user.User
import io.devist.chat.user.UserDto
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class UserEntityToDtoConverter : Converter<User, UserDto>{

    override fun convert(source: User): UserDto? {
        return UserDto(
                id = source.id,
                name = source.name,
                email = source.email,
                status = source.status
        )
    }
}