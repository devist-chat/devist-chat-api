package io.devist.chat.channel

import io.devist.chat.user.UserDto
import java.time.LocalDateTime
import java.util.*

class ChannelDto(
        var id: UUID? = null,
        var name: String?,
        var title: String?,
        var subTitle: String?,
        var status: ChannelStatus?,
        var private: Boolean?,
        var directMessage: Boolean?,
        var created: LocalDateTime?,
        var createdBy: UserDto?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelDto

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}