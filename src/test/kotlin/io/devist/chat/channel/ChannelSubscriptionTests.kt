package io.devist.chat.channel

import com.github.javafaker.Faker
import io.devist.chat.channel.exceptions.ChannelSubscriptionException
import io.devist.chat.user.UserDto
import io.devist.chat.user.UserService
import org.flywaydb.test.FlywayTestExecutionListener
import org.flywaydb.test.annotation.FlywayTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = ["classpath:application.yml"])
@Transactional
@TestExecutionListeners(
        listeners = [FlywayTestExecutionListener::class],
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@FlywayTest

class ChannelSubscriptionTests {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var channelService: ChannelService

    val faker = Faker()

    @Test
    fun `should return a empty list of channels`() {
        val user = user()

        repeat(faker.number().numberBetween(5, 10)) { channel() }

        val channelList = channelService.listChannelsByUser(user.id!!)

        assertThat(channelList, `is`(emptyList()))
    }

    @Test
    fun `should subscribe to a channel`() {
        val user = user()

        val channel1 = channel()

        val userId = user.id!!
        val channelId1 = channel1.id!!

        channelService.subscribe(userId, channelId1)

        val channelList = channelService.listChannelsByUser(user.id!!)

        assertThat(channelList, contains(channel1))
    }

    @Test
    fun `should not subscribe to a channel already subscribed`() {
        val user = user()

        val channel1 = channel()

        val userId = user.id!!
        val channelId1 = channel1.id!!

        channelService.subscribe(userId, channelId1)

        val exception = assertThrows<ChannelSubscriptionException> { channelService.subscribe(userId, channelId1) }

        assertThat(exception.message, `is`(equalTo("User ${user.name} is already subscribed on channel ${channel1.name}")))
    }

    @Test
    fun `should not subscribe to a private channel`() {
        val user = user()

        val channel1 = channel(true)

        val userId = user.id!!
        val channelId1 = channel1.id!!

        val exception = assertThrows<ChannelSubscriptionException> { channelService.subscribe(userId, channelId1) }

        assertThat(exception.message, `is`(equalTo("User ${user.name} cannot subscribe on private channel ${channel1.name}")))
    }

    @Test
    fun `should unsubscribe from a channel`() {
        val user = user()

        val channel1 = channel()
        val channel2 = channel()
        val channel3 = channel()

        channelService.subscribe(user.id!!, channel1.id!!)
        channelService.subscribe(user.id!!, channel2.id!!)
        channelService.subscribe(user.id!!, channel3.id!!)

        channelService.unsubscribe(user.id!!, channel2.id!!)

        val channelLists = channelService.listChannelsByUser(user.id!!)

        assertThat(channelLists, hasSize(2))
        assertThat(channelLists, containsInAnyOrder(channel1, channel3))
    }

    @Test
    fun `should return the members from the channel`() {
        val user1 = user()
        val user2 = user()
        val user3 = user()

        val channel1 = channel()
        val channel2 = channel()
        val channel3 = channel()

        channelService.subscribe(user1.id!!, channel1.id!!)
        channelService.subscribe(user1.id!!, channel2.id!!)
        channelService.subscribe(user1.id!!, channel3.id!!)

        channelService.subscribe(user2.id!!, channel1.id!!)

        channelService.subscribe(user3.id!!, channel1.id!!)
        channelService.subscribe(user3.id!!, channel3.id!!)

        val usersChannel1 = channelService.listUsersByChannel(channel1.id!!)
        val usersChannel2 = channelService.listUsersByChannel(channel2.id!!)
        val usersChannel3 = channelService.listUsersByChannel(channel3.id!!)

        assertThat(usersChannel1, hasSize(3))
        assertThat(usersChannel2, hasSize(1))
        assertThat(usersChannel3, hasSize(2))

        assertThat(usersChannel1, containsInAnyOrder(user1, user2, user3))
        assertThat(usersChannel2, containsInAnyOrder(user1))
        assertThat(usersChannel3, containsInAnyOrder(user1, user3))
    }

    private fun channel(private: Boolean = false): ChannelDto = channelService.create(ChannelDto(
            name = faker.name().firstName(),
            title = faker.rickAndMorty().location(),
            subTitle = faker.rickAndMorty().quote(),
            status = ChannelStatus.ACTIVE,
            createdBy = user(),
            created = LocalDateTime.now(),
            private = private,
            directMessage = faker.bool().bool()
    ))

    private fun user(): UserDto = userService.create(UserDto(
            name = faker.name().fullName(),
            email = faker.internet().emailAddress(),
            password = faker.lorem().characters(10, 20)
    ))
}