package io.devist.chat.channel.members

import io.devist.chat.channel.Channel
import io.devist.chat.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChannelMemberRepository : JpaRepository<ChannelMember, UUID> {
    @Query("SELECT c FROM Channel c JOIN ChannelMember cm ON cm.channel.id = c.id WHERE cm.user.id = :userId")
    fun listChannelsByUserId(@Param("userId") userId: UUID): List<Channel>

    @Query("SELECT u FROM User u JOIN ChannelMember cm ON cm.user.id = u.id WHERE cm.channel.id = :channelId")
    fun listUsersByChannelId(@Param("channelId") channelId: UUID): List<User>

    @Query("SELECT cm FROM ChannelMember cm WHERE cm.user.id = :userId AND cm.channel.id = :channelId")
    fun findUserSubscribedChannel(
            @Param("userId") userId: UUID,
            @Param("channelId") channelId: UUID): Optional<ChannelMember>
}