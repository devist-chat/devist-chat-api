package io.devist.chat.user

import com.github.javafaker.Faker
import io.devist.chat.user.exceptions.UserCreateException
import org.flywaydb.test.annotation.FlywayTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = ["classpath:application.yml"])
@Transactional
class UserCreationTests(
) {
    @Autowired
    private lateinit var userService: UserService

    val faker = Faker()

    @Test
    fun `should fail when try to create a user without name`() {
        assertThrows<UserCreateException>("Name is mandatory") {
            userService.validateUserName(UserDto(
                    email = faker.internet().emailAddress(),
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }

    @Test
    fun `should fail when try to create a user with empty name`() {
        assertThrows<UserCreateException>("Name is mandatory") {
            userService.validateUserName(UserDto(
                    name = "   ",
                    email = faker.internet().emailAddress(),
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }

    @Test
    fun `should fail when try to create a user without last name`() {
        assertThrows<UserCreateException>("Name should be full (firstname and lastname)") {
            userService.validateUserName(UserDto(
                    name = faker.name().firstName(),
                    email = faker.internet().emailAddress(),
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }

    @Test
    fun `should fail when try to create a user without email`() {
        assertThrows<UserCreateException>("Email is mandatory") {
            userService.validateUserEmail(UserDto(
                    name = faker.name().fullName(),
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }


    @Test
    fun `should fail when try to create a user with a invalid email (domain only)`() {
        assertThrows<UserCreateException>("Invalid email") {
            userService.validateUserEmail(UserDto(
                    name = faker.name().fullName(),
                    email = "@gmail.com",
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }

    @Test
    fun `should fail when try to create a user with a invalid email (without domain)`() {
        assertThrows<UserCreateException>("Invalid email") {
            userService.validateUserEmail(UserDto(
                    name = faker.name().fullName(),
                    email = "myemail@",
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }

    @Test
    fun `should fail when try to create a user with a invalid email (without end domain)`() {
        assertThrows<UserCreateException>("Invalid email") {
            userService.validateCreate(UserDto(
                    name = faker.name().fullName(),
                    email = "myemail@gmail",
                    password = faker.lorem().characters(10, 20)
            ))
        }
    }

    @Test
    fun `should fail when try to create a user without password`() {
        assertThrows<UserCreateException>("Password is mandatory") {
            userService.validateUserPassword(UserDto(
                    name = faker.name().fullName(),
                    email = faker.internet().emailAddress()
            ))
        }
    }

    @Test
    fun `should fail when try to create a user with password length less tan 5 characters`() {
        assertThrows<UserCreateException>("Password must have at least 5 characters") {
            userService.validateUserPassword(UserDto(
                    name = faker.name().fullName(),
                    email = faker.internet().emailAddress(),
                    password = faker.lorem().characters(1, 4)
            ))
        }
    }

    @Test
    fun `should pass verification`() {
        userService.validateCreate(UserDto(
                name = faker.name().fullName(),
                email = faker.internet().emailAddress(),
                password = faker.lorem().characters(10, 20)
        ))
    }

    @Test
    @FlywayTest
    fun `should create a user and let the status NEW`() {
        userService.create(UserDto(
                name = faker.name().fullName(),
                email = faker.internet().emailAddress(),
                password = faker.lorem().characters(10, 20)
        ))
    }
}