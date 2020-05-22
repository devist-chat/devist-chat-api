package io.devist.chat.channel.converters

import io.devist.chat.channel.Channel
import io.devist.chat.channel.ChannelDto
import io.devist.chat.user.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class ChannelEntityToDtoConverter : Converter<Channel, ChannelDto> {

    @Autowired
    @Lazy
    lateinit var conversionService: ConversionService

    override fun convert(source: Channel): ChannelDto? {
        return ChannelDto(
                id = source.id,
                name = source.name,
                title = source.title,
                subTitle = source.subTitle,
                status = source.status,
                created = source.created,
                createdBy = conversionService.convert(source.createdBy, UserDto::class.java),
                directMessage = source.directMessage,
                private = source.private
        )
    }
}