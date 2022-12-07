package dev.schulzke

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.ceil

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
        else max(numberOfWeeks(startOfWeek), min)

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
                if (includeYearInName) "${month.name} $year"
                else month.name,
            )
        }
    }
}

private fun DayOfWeek.distanceTo(other: DayOfWeek): Int {
    return abs(other.value - this.value)
}

@Serializable
class CalDay(
    val date: LocalDate,
)

@Serializable
data class CalendarConfig(
    val startOfWeek: DayOfWeek,
    val holidays: Map<LocalDate, List<String>>
)

val json = Json {
    ignoreUnknownKeys = true
}

fun Application.calendarRoutes() {
    routing {
        static {
            files("static")
        }

        get("/cal/{config}/{year}") {
            val configPath = call.parameters["config"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val config = json.decodeFromString<CalendarConfig>(File("configurations/${configPath}/config.json").readText())
            if (year == null) {
                call.respondText(text = "400: Bad Request", status = HttpStatusCode.BadRequest)
            } else {
                val calYear = CalYear.of(year)
                call.respondHtml { calendarTemplate(calYear, config) }
            }
        }
    }
}
