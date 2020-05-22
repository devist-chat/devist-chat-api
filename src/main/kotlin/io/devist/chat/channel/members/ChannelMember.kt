package io.devist.chat.channel.members

import io.devist.chat.channel.Channel
import io.devist.chat.user.User
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(schema = "devist", name = "channel_members")
class ChannelMember(
        @Id
        @GeneratedValue
        val id: UUID? = null,

        @JoinColumn(name = "user_id")
        @ManyToOne
        var user: User,

        @JoinColumn(name = "channel_id")
        @ManyToOne
        var channel: Channel,

        @Column(name = "last_message_read_datetime")
        var lastMessageReadDatetime: LocalDateTime
)