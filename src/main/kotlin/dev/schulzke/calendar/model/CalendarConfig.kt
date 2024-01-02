package dev.schulzke.calendar.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CalendarConfig(
    val id: String,
    val startOfWeek: DayOfWeek,
    val includeExtraMonth: Boolean = true,
    val coverImage: String? = null,
    val coverAttribution: String? = null,
    val months: Map<String, MonthConfig>,
    val holidays: Map<LocalDate, List<String>>,
    val events: Map<LocalDate, List<String>>,
)

@Serializable
data class MonthConfig(
    val image: String? = null,
    val imageAttribution: String? = null,
    val quote: Quote? = null,
)

@Serializable
data class Quote(
    val text: String,
    val author: String? = null,
    val work: String? = null,
    val widthOverride: Int? = null,
)
