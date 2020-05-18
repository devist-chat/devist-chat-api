package io.devist.chat.user

import com.github.javafaker.Faker
import io.devist.chat.user.exceptions.UserActivationException
import io.devist.chat.user.exceptions.UserNotFoundException
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
class UserActivationTests {

    @Autowired
    private lateinit var userService: UserService

    val faker = Faker()

    @Test
    fun `should return a not empty hash verification`() {
        assertThat(user().emailVerificationHash, `is`(not((emptyOrNullString()))));
    }

    @Test
    fun `should activate the user by the hash`() {
        val user = user()

        userService.activate(user.emailVerificationHash)

        val userRetrieved = userService[user.id]

        assertThat(userRetrieved.id, `is`(equalTo(user.id)))
        assertThat(userRetrieved.status, `is`(equalTo(UserStatus.ACTIVE)))
    }

    @Test
    fun `should fire a exception when passing a empty hash`() {
        assertThrows<UserActivationException> { userService.validateHash("") }
    }

    @Test
    fun `should throw an exception when passing an invalid or nonexistent hash`() {
        assertThrows<UserNotFoundException> { userService.activate(faker.random().hex()) }
    }

    @Test
    fun `should throw an exception when passing a already activated user`() {
        val user = user()

        userService.activate(user.emailVerificationHash)

        assertThrows<UserActivationException> { userService.activate(user.emailVerificationHash) }
    }

    @Test
    fun `should throw an exception when try to activate a inactive user`() {
        val user = user()

        userService.deactivate(user.id)

        assertThrows<UserActivationException> { userService.activate(user.emailVerificationHash) }
    }

    private fun user(): UserDto = userService.create(UserDto(
            name = faker.name().fullName(),
            email = faker.internet().emailAddress(),
            password = faker.lorem().characters(10, 20)
    ))
}