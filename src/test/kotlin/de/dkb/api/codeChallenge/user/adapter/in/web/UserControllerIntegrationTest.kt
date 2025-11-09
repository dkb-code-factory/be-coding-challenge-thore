package de.dkb.api.codeChallenge.user.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.NotificationType
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.NotifyUserRequest
import de.dkb.api.codeChallenge.user.adapter.`in`.web.dto.RegisterUserRequest
import de.dkb.api.codeChallenge.user.adapter.out.persistence.JpaNotificationCategoryRepository
import de.dkb.api.codeChallenge.user.adapter.out.persistence.JpaUserRepository
import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.NotificationCategoryEntity
import de.dkb.api.codeChallenge.user.adapter.out.persistence.entity.UserEntity
import de.dkb.api.codeChallenge.user.domain.NotificationCategory
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

    @Autowired
    private lateinit var jpaNotificationCategoryRepository: JpaNotificationCategoryRepository

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
        jpaNotificationCategoryRepository.deleteAll()
        jpaUserRepository.deleteAll()
    }

    @Test
    fun registerUserStoresCategoriesCorrectly() {
        // Given
        val userId = UUID.randomUUID()
        val request = createRegisterUserRequest(
            id = userId,
            notifications = setOf(NotificationType.TYPE_1, NotificationType.TYPE_2, NotificationType.TYPE_3)
        )

        // When
        mockMvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.notifications").isArray)
            .andExpect(jsonPath("$.notifications.length()").value(4)) // CATEGORY_A contains TYPE_1, TYPE_2, TYPE_3, and TYPE_6

        // Then
        val savedUser = jpaUserRepository.findById(userId)
        assertThat(savedUser).isPresent
        val categories = jpaNotificationCategoryRepository.findByUserId(userId)
        assertThat(categories).hasSize(1)
        assertThat(categories.first().category).isEqualTo(NotificationCategory.CATEGORY_A.name)
    }

    @Test
    fun sendNotificationSentWhenUserSubscribed() {
        // Given
        val userId = UUID.randomUUID()
        jpaUserRepository.save(UserEntity(id = userId))
        jpaNotificationCategoryRepository.save(
            NotificationCategoryEntity(userId, NotificationCategory.CATEGORY_A.name)
        )
        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_1,
            message = "Important update!"
        )

        // When
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isNoContent)
        }

        // Then
        assertThat(output).contains("Sending notification of type TYPE_1")
    }

    @Test
    fun sendNotificationReturnsNotFoundWhenUserDoesNotExist() {
        // Given
        val userId = UUID.randomUUID()
        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_1,
            message = "Important update!"
        )

        // When & Then
        mockMvc.perform(
            post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun sendNotificationReturnsUnprocessableEntityWhenUserNotSubscribed() {
        // Given
        val userId = UUID.randomUUID()
        jpaUserRepository.save(UserEntity(id = userId))
        jpaNotificationCategoryRepository.save(
            NotificationCategoryEntity(userId, NotificationCategory.CATEGORY_A.name)
        )
        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_4, // User is subscribed to CATEGORY_A, not CATEGORY_B
            message = "Important update!"
        )

        // When & Then
        mockMvc.perform(
            post("/notify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toJson())
        )
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun userRegisteredWithType1ShouldReceiveType6Notifications() {
        // Given - User registered with TYPE_1 (which maps to CATEGORY_A)
        val userId = UUID.randomUUID()
        jpaUserRepository.save(UserEntity(id = userId))
        jpaNotificationCategoryRepository.save(
            NotificationCategoryEntity(userId, NotificationCategory.CATEGORY_A.name)
        )
        
        // When - Send TYPE_6 notification (TYPE_6 is also in CATEGORY_A)
        val request = createNotifyUserRequest(
            userId = userId,
            notificationType = NotificationType.TYPE_6,
            message = "New TYPE_6 notification!"
        )
        
        val output = captureConsoleOutput {
            mockMvc.perform(
                post("/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request.toJson())
            )
                .andExpect(status().isNoContent)
        }

        // Then - Notification should be sent successfully
        assertThat(output).contains("Sending notification of type TYPE_6")
        assertThat(output).contains("to user $userId")
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
