package de.dkb.api.codeChallenge.user.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import de.dkb.api.codeChallenge.user.adapter.`in`.api.NotificationType
import de.dkb.api.codeChallenge.user.adapter.`in`.api.NotifyUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.api.RegisterUserRequest
import de.dkb.api.codeChallenge.user.adapter.out.persistence.JpaUserRepository
import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import de.dkb.api.codeChallenge.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.*

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jpaUserRepository: JpaUserRepository

    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:15")

        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @AfterEach
    fun tearDown() {
        jpaUserRepository.deleteAll()
    }

    @Test
    fun `registerUser - given valid user registration request - when posting to register endpoint - then user is created and returned`() {
        // Given
        val userId = UUID.randomUUID()
        val request = createRegisterUserRequest(
            id = userId,
            notifications = setOf(NotificationType.TYPE_1, NotificationType.TYPE_2, NotificationType.TYPE_3)
        )

        // When & Then
        mockMvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.notifications").isArray)
            .andExpect(jsonPath("$.notifications.length()").value(3))

        // Verify user is persisted in database
        val savedUser = jpaUserRepository.findById(userId)
        assertThat(savedUser).isPresent
        assertThat(savedUser.get().id).isEqualTo(userId)
        assertThat(savedUser.get().notifications).hasSize(3)
        assertThat(savedUser.get().notifications).contains(
            "type1",
            "type2",
            "type3"
        )
    }

    @Test
    fun `registerUser - given user with single notification type - when posting to register endpoint - then user is created correctly`() {
        // Given
        val userId = UUID.randomUUID()
        val request = createRegisterUserRequest(
            id = userId,
            notifications = setOf(NotificationType.TYPE_5)
        )

        // When & Then
        mockMvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.notifications").isArray)
            .andExpect(jsonPath("$.notifications.length()").value(1))

        // Verify user is persisted in database
        val savedUser = jpaUserRepository.findById(userId)
        assertThat(savedUser).isPresent
        assertThat(savedUser.get().notifications).hasSize(1)
        assertThat(savedUser.get().notifications).contains("type5")
    }

    @Test
    fun `registerUser - given user with empty notifications - when posting to register endpoint - then user is created with no subscriptions`() {
        // Given
        val userId = UUID.randomUUID()
        val request = createRegisterUserRequest(
            id = userId,
            notifications = emptySet()
        )

        // When & Then
        mockMvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.notifications").isArray)
            .andExpect(jsonPath("$.notifications.length()").value(0))

        // Verify user is persisted in database
        val savedUser = jpaUserRepository.findById(userId)
        assertThat(savedUser).isPresent
        assertThat(savedUser.get().notifications).isEmpty()
    }

    @Test
    fun `registerUser - given user with all notification types - when posting to register endpoint - then user is created with all subscriptions`() {
        // Given
        val userId = UUID.randomUUID()
        val request = createRegisterUserRequest(
            id = userId,
            notifications = setOf(
                NotificationType.TYPE_1,
                NotificationType.TYPE_2,
                NotificationType.TYPE_3,
                NotificationType.TYPE_4,
                NotificationType.TYPE_5
            )
        )

        // When & Then
        mockMvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.notifications").isArray)
            .andExpect(jsonPath("$.notifications.length()").value(5))

        // Verify user is persisted in database
        val savedUser = jpaUserRepository.findById(userId)
        assertThat(savedUser).isPresent
        assertThat(savedUser.get().notifications).hasSize(5)
    }

    @Test
    fun `sendNotification - given user subscribed to notification type - when posting to notify endpoint - then notification is sent successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity(
            id = userId,
            notifications = mutableSetOf("type1", "type2", "type3")
        )
        jpaUserRepository.save(user)

        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_1,
            message = "Important update for you!"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isOk)
        }

        // Then
        assertThat(output).contains("Sending notification of type TYPE_1")
        assertThat(output).contains("to user $userId")
        assertThat(output).contains("Important update for you!")
    }

    @Test
    fun `sendNotification - given user not subscribed to notification type - when posting to notify endpoint - then notification is not sent`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity(
            id = userId,
            notifications = mutableSetOf("type1", "type2", "type3")
        )
        jpaUserRepository.save(user)

        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_4, // User not subscribed to type4
            message = "This should not be sent"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isOk)
        }

        // Then
        assertThat(output).doesNotContain("Sending notification")
    }

    @Test
    fun `sendNotification - given non-existing user - when posting to notify endpoint - then notification is not sent`() {
        // Given
        val nonExistingUserId = UUID.randomUUID()
        val request = createNotifyUserRequest(
            userId = nonExistingUserId,
            notificationType = NotificationType.TYPE_1,
            message = "This should not be sent"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isOk)
        }

        // Then
        assertThat(output).doesNotContain("Sending notification")
    }

    @Test
    fun `sendNotification - given user subscribed to type2 - when posting notification of type2 - then notification is sent successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity(
            id = userId,
            notifications = mutableSetOf("type1", "type2", "type3")
        )
        jpaUserRepository.save(user)

        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_2,
            message = "Type 2 notification message"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isOk)
        }

        // Then
        assertThat(output).contains("Sending notification of type TYPE_2")
        assertThat(output).contains("to user $userId")
        assertThat(output).contains("Type 2 notification message")
    }

    @Test
    fun `sendNotification - given user subscribed to type3 - when posting notification of type3 - then notification is sent successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity(
            id = userId,
            notifications = mutableSetOf("type1", "type2", "type3")
        )
        jpaUserRepository.save(user)

        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_3,
            message = "Type 3 notification message"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isOk)
        }

        // Then
        assertThat(output).contains("Sending notification of type TYPE_3")
        assertThat(output).contains("to user $userId")
        assertThat(output).contains("Type 3 notification message")
    }

    @Test
    fun `sendNotification - given user subscribed to specific types - when posting notification of type5 - then notification is not sent`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity(
            id = userId,
            notifications = mutableSetOf("type1", "type2", "type3")
        )
        jpaUserRepository.save(user)

        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_5, // User not subscribed to type5
            message = "This should not be sent"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isOk)
        }

        // Then
        assertThat(output).doesNotContain("Sending notification")
    }

     private fun createRegisterUserRequest(
        id: UUID = UUID.randomUUID(),
        notifications: Set<NotificationType> = emptySet()
    ): RegisterUserRequest {
        return RegisterUserRequest(
            id = id,
            notifications = notifications
        )
    }

    private fun createNotifyUserRequest(
        userId: UUID,
        notificationType: NotificationType,
        message: String
    ): NotifyUserRequest {
        return NotifyUserRequest(
            userId = userId,
            notificationType = notificationType,
            message = message
        )
    }

    private fun RegisterUserRequest.toJson(): String {
        return objectMapper.writeValueAsString(this)
    }

    private fun NotifyUserRequest.toJson(): String {
        return objectMapper.writeValueAsString(this)
    }

    private fun captureConsoleOutput(block: () -> Unit): String {
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        try {
            block()
            return outputStream.toString()
        } finally {
            System.setOut(originalOut)
        }
    }
}
