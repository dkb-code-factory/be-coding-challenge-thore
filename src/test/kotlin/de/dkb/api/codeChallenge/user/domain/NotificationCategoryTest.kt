package de.dkb.api.codeChallenge.user.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class NotificationCategoryTest {

    @ParameterizedTest(name = "{0} should map to {1}")
    @MethodSource("notificationTypeToCategoryMapping")
    fun `all notification types map to exactly one category`(
        notificationType: NotificationType,
        expectedCategory: NotificationCategory
    ) {
        // When
        val actualCategory = NotificationCategory.fromNotificationType(notificationType)

        // Then
        assertThat(actualCategory).isEqualTo(expectedCategory)
    }

    @ParameterizedTest(name = "{0} contains {1}")
    @MethodSource("notificationTypeToCategoryMapping")
    fun `category contains its notification types`(
        notificationType: NotificationType,
        category: NotificationCategory
    ) {
        // Then
        assertThat(category.types).contains(notificationType)
        assertThat(category.containsType(notificationType)).isTrue()
    }

    companion object {
        @JvmStatic
        fun notificationTypeToCategoryMapping(): Stream<Arguments> = Stream.of(
            Arguments.of(NotificationType.TYPE_1, NotificationCategory.CATEGORY_A),
            Arguments.of(NotificationType.TYPE_2, NotificationCategory.CATEGORY_A),
            Arguments.of(NotificationType.TYPE_3, NotificationCategory.CATEGORY_A),
            Arguments.of(NotificationType.TYPE_6, NotificationCategory.CATEGORY_A),
            Arguments.of(NotificationType.TYPE_4, NotificationCategory.CATEGORY_B),
            Arguments.of(NotificationType.TYPE_5, NotificationCategory.CATEGORY_B)
        )
    }
}
