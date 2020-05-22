package io.devist.chat.channel

import com.github.javafaker.Faker
import io.devist.chat.channel.exceptions.ChannelCreateException
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
import java.time.LocalDate
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
class ChannelCreationTests {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var channelService: ChannelService

    val faker = Faker()

    @Test
    fun `should create a channel`() {
        val user = user()

        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user,
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val created = channelService.create(channel)

        assertThat(created, `is`(not(nullValue())))
        assertThat(created.id, `is`(not(nullValue())))
    }

    @Test
    fun `should not create a channel with a null name`() {
        val channel = ChannelDto(
                name = null,
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Name is mandatory")))
    }


    @Test
    fun `should not create a channel with a empty string name`() {
        val channel = ChannelDto(
                name = "  ",
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Name is mandatory")))
    }

    @Test
    fun `should not create a channel with a null title`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = null,
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Title is mandatory")))
    }

    @Test
    fun `should not create a channel with a empty string title`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = "   ",
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Title is mandatory")))
    }

    @Test
    fun `should not create a channel with a null status`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = null,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Channel status is mandatory")))
    }

    @Test
    fun `should not create a channel with a null user`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = null,
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("User creation is mandatory")))
    }

    @Test
    fun `should not create a channel with a null creation date`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = null,
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Creation date is mandatory")))
    }

    @Test
    fun `should not create a channel with a null private field`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = null,
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Private channel is mandatory")))
    }

    @Test
    fun `should not create a channel with a null direct message field`() {
        val channel = ChannelDto(
                name = faker.name().firstName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = null
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Direct message is mandatory")))
    }

    @Test
    fun `should not create a channel with a spaced name`() {
        val channel = ChannelDto(
                name = faker.name().fullName(),
                title = faker.rickAndMorty().location(),
                subTitle = faker.rickAndMorty().quote(),
                status = ChannelStatus.ACTIVE,
                createdBy = user(),
                created = LocalDateTime.now(),
                private = faker.bool().bool(),
                directMessage = faker.bool().bool()
        )

        val channelCreateException = assertThrows<ChannelCreateException> { channelService.create(channel) }

        assertThat(channelCreateException.message, `is`(equalTo("Name should not contain spaces")))
    }

    private fun user(): UserDto = userService.create(UserDto(
            name = faker.name().fullName(),
            email = faker.internet().emailAddress(),
            password = faker.lorem().characters(10, 20)
    ))
}