package io.devist.chat.channel

import com.github.javafaker.Faker
import io.devist.chat.user.UserDto
import io.devist.chat.user.UserService
import org.flywaydb.test.FlywayTestExecutionListener
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.assertThrows


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
class ChannelUpdateTests {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var channelService: ChannelService

    val faker = Faker()

    @Test
    fun `should update the channel`() {
        val channel = channel()

        channel.name = faker.name().firstName()

        val updatedChannel = channelService.update(channel)

        assertThat(updatedChannel, `is`(notNullValue()))
        assertThat(updatedChannel.name, `is`(equalTo(channel.name)))
        assertThat(updatedChannel.title, `is`(equalTo(channel.title)))
        assertThat(updatedChannel.subTitle, `is`(equalTo(channel.subTitle)))
        assertThat(updatedChannel.status, `is`(equalTo(channel.status)))
        assertThat(updatedChannel.private, `is`(equalTo(channel.private)))
        assertThat(updatedChannel.directMessage, `is`(equalTo(channel.directMessage)))
        assertThat(updatedChannel.created, `is`(equalTo(channel.created)))
        assertThat(updatedChannel.createdBy?.id, `is`(equalTo(channel.createdBy?.id)))
    }

    @Test
    fun `should not update the created by user`() {
        val channel = channel()

        val userOriginal = channel.createdBy

        channel.createdBy = user()

        val updatedChannel = channelService.update(channel)

        assertThat(updatedChannel.createdBy?.id, `is`(equalTo(userOriginal?.id)))
    }

    @Test
    fun `should not update the created date`() {
        val channel = channel()

        val dateCreation = channel.created

        channel.created = LocalDateTime.now()

        val updatedChannel = channelService.update(channel)

        assertThat(updatedChannel.created, `is`(equalTo(dateCreation)))
    }

    private fun channel(): ChannelDto = channelService.create(ChannelDto(
            name = faker.name().firstName(),
            title = faker.rickAndMorty().location(),
            subTitle = faker.rickAndMorty().quote(),
            status = ChannelStatus.ACTIVE,
            createdBy = user(),
            created = LocalDateTime.now(),
            private = faker.bool().bool(),
            directMessage = faker.bool().bool()
    ))

    private fun user(): UserDto = userService.create(UserDto(
            name = faker.name().fullName(),
            email = faker.internet().emailAddress(),
            password = faker.lorem().characters(10, 20)
    ))
}