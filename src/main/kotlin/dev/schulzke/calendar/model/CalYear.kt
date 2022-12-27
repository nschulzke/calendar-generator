package dev.schulzke.calendar.model

import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
class CalYear private constructor(
    val year: Int,
    private val months: List<CalMonth>,
) : Iterable<CalMonth> by months {
    companion object {
        fun of(year: Int, extraMonth: Boolean): CalYear {
            return CalYear(year, Month.values().mapIndexed { index, month ->
                CalMonth.of(year, month, "${(index + 1).toString().padStart(2, '0')}.${month.name.lowercase()}")
            } + (if (extraMonth) listOf(CalMonth.of(year + 1, Month.JANUARY, "13.january", includeYearInName = true)) else emptyList()))
        }
    }
}