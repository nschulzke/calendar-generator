package dev.schulzke.calendar.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class CalDay(
    val date: LocalDate,
)