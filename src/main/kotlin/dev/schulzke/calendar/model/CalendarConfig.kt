package dev.schulzke.calendar.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CalendarConfig(
    val id: String,
    val startOfWeek: DayOfWeek,
    val quotes: Map<String, Quote>,
    val holidays: Map<LocalDate, List<String>>,
    val events: Map<LocalDate, List<String>>,
)

@Serializable
data class Quote(
    val text: String,
    val author: String,
    val work: String? = null,
    val widthOverride: Int? = null,
)
