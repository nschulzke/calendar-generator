package dev.schulzke.calendar.model

import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
class CalYear private constructor(
    private val months: List<CalMonth>,
) : Iterable<CalMonth> by months {
    companion object {
        fun of(year: Int): CalYear {
            return CalYear(Month.values().map {
                CalMonth.of(year, it)
            } + listOf(CalMonth.of(year + 1, Month.JANUARY, includeYearInName = true)))
        }
    }
}