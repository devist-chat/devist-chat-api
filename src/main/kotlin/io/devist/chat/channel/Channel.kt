package io.devist.chat.channel

import io.devist.chat.user.User
import io.devist.chat.user.UserStatus
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(schema = "devist", name = "channel")
class Channel(
        @Id
        @GeneratedValue
        var id: UUID? = null,

        var name: String,

        var title: String,

        @Column(name = "sub_title")
        var subTitle: String,

        @NotNull
        var status: ChannelStatus,

        @NotNull
        var private: Boolean,

        @NotNull
        @Column(name = "direct_message")
        var directMessage: Boolean,

        var created: LocalDateTime,

        @JoinColumn(name = "created_by")
        @ManyToOne
        var createdBy: User
) {

}