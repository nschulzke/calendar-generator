package dev.schulzke.calendar.model

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import java.time.format.TextStyle
import java.util.*
import kotlin.math.ceil

@Serializable
class CalMonth private constructor(
    val previousMonth: CalMonth?,
    val nextMonth: CalMonth?,
    private val days: List<CalDay>,
    val name: String,
) : Iterable<CalDay> by days {
    val startingWeekday: DayOfWeek
        get() = days.first().date.dayOfWeek

    val endingWeekday: DayOfWeek
        get() = days.last().date.dayOfWeek

    val numberOfDays: Int
        get() = days.size

    fun numberOfWeeks(startOfWeek: DayOfWeek, min: Int? = null): Int =
         if (min === null) ceil((initialPaddingSize(startOfWeek) + numberOfDays) / 7.0).toInt()
        else Integer.max(numberOfWeeks(startOfWeek), min)

    fun previewsAtStart(startOfWeek: DayOfWeek): Boolean =
        initialPaddingSize(startOfWeek) >= 2

    fun initialPaddingSize(startOfWeek: DayOfWeek): Int =
        startOfWeek.distanceTo(startingWeekday)

    companion object {
        fun of(year: Int, month: Month, includePrevious: Boolean = true, includeYearInName: Boolean = false): CalMonth {
            val days = mutableListOf<CalDay>()
            var currentDay = LocalDate(year, month, 1)
            while (currentDay.month == month) {
                days.add(CalDay(currentDay))
                currentDay = currentDay.plus(1, DateTimeUnit.DAY)
            }
            return CalMonth(
                previousMonth =
                if (!includePrevious) null
                else if (month == Month.JANUARY) of(
                    year = year - 1,
                    month = month - 1,
                    includePrevious = false,
                    includeYearInName = true,
                )
                else of(year, month - 1, false),
                nextMonth =
                if (!includePrevious) null
                else if (month == Month.DECEMBER) of(
                    year = year + 1,
                    month = month + 1,
                    includePrevious = false,
                    includeYearInName = true,
                )
                else of(year, month + 1, false),
                days = days,
                name =
                if (includeYearInName) "${month.getDisplayName(TextStyle.FULL, Locale.US)} $year"
                else month.getDisplayName(TextStyle.FULL, Locale.US),
            )
        }
    }
}