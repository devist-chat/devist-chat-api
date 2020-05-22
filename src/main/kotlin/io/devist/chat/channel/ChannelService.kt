package io.devist.chat.channel

import io.devist.chat.channel.exceptions.ChannelCreateException
import io.devist.chat.channel.exceptions.ChannelNotFoundException
import io.devist.chat.channel.exceptions.ChannelSubscriptionException
import io.devist.chat.channel.members.ChannelMember
import io.devist.chat.channel.members.ChannelMemberRepository
import io.devist.chat.user.UserDto
import io.devist.chat.user.UserRepository
import io.devist.chat.user.UserService
import io.devist.chat.user.exceptions.UserNotFoundException
import io.devist.chat.utils.messages.MessageService
import io.devist.chat.utils.validators.Validators
import io.devist.chat.utils.validators.isSingleWord
import io.devist.chat.utils.validators.isValid
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList

@Service
class ChannelService(
        var conversionService: ConversionService,
        var channelRepository: ChannelRepository,
        var channelMemberRepository: ChannelMemberRepository,
        var userRepository: UserRepository,
        val messages: MessageService,
        var validators: Validators,
        var userService: UserService
) {

    fun create(channelDto: ChannelDto): ChannelDto {

        validateCreation(channelDto)

        val entity = conversionService.convert(channelDto, Channel::class.java)

        val saved = channelRepository.save(entity!!)

        return conversionService.convert(saved, ChannelDto::class.java)!!
    }

    fun update(channelDto: ChannelDto): ChannelDto {
        validateUpdate(channelDto)

        val entity = conversionService.convert(channelDto, Channel::class.java)

        val original = entity?.id?.let { channelRepository.findById(it).orElseThrow { ChannelNotFoundException(messages["exceptions.ChannelNotFoundException.byId", it]) } }!!

        entity.created = original.created
        entity.createdBy = original.createdBy

        val saved = channelRepository.save(entity)

        return conversionService.convert(saved, ChannelDto::class.java)!!
    }

    fun subscribe(userId: UUID, channelId: UUID) {
        val user = findUser(userId)
        val channel = findChannel(channelId)

        if (isUserSubscribed(userId, channelId).isPresent)
            throw ChannelSubscriptionException(messages["exception.ChannelSubscriptionException.user.alreadySubscribed", user.name, channel.name])

        if (channel.private)
            throw ChannelSubscriptionException(messages["exception.ChannelSubscriptionException.private", user.name, channel.name])

        channelMemberRepository.save(ChannelMember(
                user = user,
                channel = channel,
                lastMessageReadDatetime = LocalDateTime.now()
        ))
    }

    fun unsubscribe(userId: UUID, channelId: UUID) {
        val userSubscribed = isUserSubscribed(userId, channelId)

        if (userSubscribed.isEmpty) throw ChannelSubscriptionException(messages["exception.ChannelSubscriptionException.user.notSubscribed"])

        channelMemberRepository.delete(userSubscribed.get())
    }

    fun isUserSubscribed(userId: UUID, channelId: UUID) = channelMemberRepository.findUserSubscribedChannel(userId, channelId)

    fun listChannelsByUser(userId: UUID): List<ChannelDto> {
        return channelMemberRepository
                .listChannelsByUserId(userId)
                .stream()
                .map { conversionService.convert(it, ChannelDto::class.java)!! }
                .toList()
    }

    fun listUsersByChannel(channelId: UUID): List<UserDto> {
        return channelMemberRepository
                .listUsersByChannelId(channelId)
                .map { conversionService.convert(it, UserDto::class.java)!! }
                .toList()
    }

    fun validateCreation(channelDto: ChannelDto) {
        validate(channelDto)

        if (!validators.isValid(channelDto.created)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.created.empty"])
        validateUserCreation(channelDto)
    }

    fun validateUpdate(channelDto: ChannelDto) {
        if (!validators.isValid(channelDto.id)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.id.empty"])
        validate(channelDto)
    }

    private fun findChannel(channelId: UUID) =
            channelRepository.findById(channelId).orElseThrow { ChannelNotFoundException(messages["exception.ChannelNotFoundException.byId"]) }

    private fun findUser(userId: UUID) =
            userRepository.findById(userId).orElseThrow { UserNotFoundException(messages["exception.UserNotFoundException.byId", userId]) }

    private fun validate(channelDto: ChannelDto) {
        if (!validators.isValid(channelDto.name)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.name.empty"])
        if (!validators.isSingleWord(channelDto.name)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.name.singleWord"])
        if (!validators.isValid(channelDto.title)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.title.empty"])
        if (!validators.isValid(channelDto.directMessage)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.directMessage.empty"])
        if (!validators.isValid(channelDto.private)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.private.empty"])
        if (!validators.isValid(channelDto.status)) throw ChannelCreateException(messages["exceptions.ChannelCreateException.status.empty"])
    }

    private fun validateUserCreation(channelDto: ChannelDto) {
        try {
            userService[channelDto.createdBy?.id]
        } catch (e: UserNotFoundException) {
            throw ChannelCreateException(messages["exceptions.ChannelCreateException.user.empty"])
        }
    }
}