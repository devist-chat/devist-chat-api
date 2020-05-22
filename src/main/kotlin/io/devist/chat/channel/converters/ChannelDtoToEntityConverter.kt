package io.devist.chat.channel.converters

import io.devist.chat.channel.Channel
import io.devist.chat.channel.ChannelDto
import io.devist.chat.user.User
import io.devist.chat.user.UserDto
import io.devist.chat.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ChannelDtoToEntityConverter : Converter<ChannelDto, Channel> {

    @Autowired
    @Lazy
    lateinit var userRepository: UserRepository

    override fun convert(source: ChannelDto): Channel? {
        val user = source.createdBy?.let { user ->
            user.id?.let { userId ->
                userRepository.findById(userId).orElse(null)
            }
        }

        return Channel(
                id = source.id,
                name = source.name!!,
                title = source.title!!,
                subTitle = source.subTitle!!,
                status = source.status!!,
                created = source.created!!,
                createdBy = user!!,
                directMessage = source.directMessage!!,
                private = source.private!!
        )
    }
}