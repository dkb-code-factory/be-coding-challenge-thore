package de.dkb.api.codeChallenge.user.adapter.out.persistence.entity

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class NotificationStringSetConverter : AttributeConverter<MutableSet<String>, String> {

    override fun convertToDatabaseColumn(valueSet: MutableSet<String>?): String =
        valueSet?.joinToString(separator = ";")
            ?: ""

    override fun convertToEntityAttribute(databaseString: String?): MutableSet<String> =
        databaseString
            ?.split(";")
            ?.filter { it.isNotBlank() }
            ?.toMutableSet()
            ?: mutableSetOf()
}
