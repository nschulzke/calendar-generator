package dev.schulzke

import dev.schulzke.calendar.model.CalMonth
import dev.schulzke.calendar.model.CalYear
import dev.schulzke.calendar.model.CalendarConfig
import kotlinx.html.*
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

fun HtmlBlockTag.month(
    month: CalMonth,
    classes: String,
    config: CalendarConfig,
    fullMonth: Boolean = false,
    minWeeks: Int? = null,
) {
    div(classes = "month $classes weeks-${month.numberOfWeeks(config.startOfWeek, minWeeks)}") {
        div(classes = "heading") {
            div(classes = "month-name") {
                +month.name
            }
            val quote = config.quotes[month.id]
            if (quote !== null) {
                div(classes = "quote") {
                    if (quote.widthOverride != null) {
                        style = "--width: ${quote.widthOverride}ch;"
                    }
                    div(classes = "quote-text") {
                        unsafe {
                            +quote.text
                        }
                    }
                    if (quote.author != null ) {
                        div(classes = "quote-attribution") {
                            span(classes = "quote-author") {
                                +quote.author
                            }
                            if (quote.work != null) {
                                span(classes = "quote-work") {
                                    +quote.work
                                }
                            }
                        }
                    }
                }
            }
        }
        if (fullMonth) {
            div(classes = "weekdays") {
                val weekDays = ((config.startOfWeek.value - 1)..(5 + config.startOfWeek.value)).map { DayOfWeek.of(it % 7 + 1) }
                for (day in weekDays) {
                    div(classes = "weekday ${day.name.lowercase()}") {
                        +day.getDisplayName(TextStyle.FULL, Locale.US)
                    }
                }
            }
        }
        div(classes = "month-grid") {
            val dayIter = month.iterator()
            var fieldsFilled = 0
            var dayOfWeek = config.startOfWeek
            if (month.previewsAtStart(config.startOfWeek)) {
                if (month.previousMonth != null && month.nextMonth != null) {
                    val maxWeeks = maxOf(
                        month.previousMonth.numberOfWeeks(config.startOfWeek),
                        month.nextMonth.numberOfWeeks(config.startOfWeek)
                    )
                    month(month.previousMonth, "preview ${dayOfWeek.name.lowercase()}", config, minWeeks = maxWeeks)
                    fieldsFilled++
                    dayOfWeek = dayOfWeek.plus(1)
                    month(month.nextMonth, "preview ${dayOfWeek.name.lowercase()}", config, minWeeks = maxWeeks)
                    fieldsFilled++
                    dayOfWeek = dayOfWeek.plus(1)
                }
            }
            while (dayOfWeek != month.startingWeekday) {
                div(classes = "day ${dayOfWeek.name.lowercase()}") {
                    +" "
                }
                fieldsFilled++
                dayOfWeek = dayOfWeek.plus(1)
            }
            while (dayIter.hasNext()) {
                val day = dayIter.next()
                div(classes = "day ${dayOfWeek.name.lowercase()}") {
                    div(classes = "day-label") {
                        +day.date.dayOfMonth.toString()
                    }
                    val events = config.events[day.date]
                    if (fullMonth && events?.isNotEmpty() == true) {
                        div(classes = "events") {
                            for (event in events) {
                                div(classes = "event") {
                                    +event
                                }
                            }
                        }
                    }
                    val holidays = config.holidays[day.date]
                    if (fullMonth && holidays?.isNotEmpty() == true) {
                        div(classes = "holidays") {
                            for (holiday in holidays) {
                                div(classes = "holiday") {
                                    +holiday
                                }
                            }
                        }
                    }
                }
                fieldsFilled++
                dayOfWeek = dayOfWeek.plus(1)
            }
            var endPadding = (7 - (fieldsFilled % 7)) % 7
            if (minWeeks != null && month.numberOfWeeks(config.startOfWeek) < minWeeks) {
                endPadding += 7
            }
            while (endPadding > 2) {
                div(classes = "day ${dayOfWeek.name.lowercase()}") {
                    +" "
                }
                endPadding--
                dayOfWeek = dayOfWeek.plus(1)
            }
            if (!month.previewsAtStart(config.startOfWeek)) {
                if (month.previousMonth != null && month.nextMonth != null) {
                    val maxWeeks = maxOf(
                        month.previousMonth.numberOfWeeks(config.startOfWeek),
                        month.nextMonth.numberOfWeeks(config.startOfWeek)
                    )
                    month(month.previousMonth, "preview ${dayOfWeek.name.lowercase()}", config, minWeeks = maxWeeks)
                    dayOfWeek = dayOfWeek.plus(1)
                    month(month.nextMonth, "preview ${dayOfWeek.name.lowercase()}", config, minWeeks = maxWeeks)
                    dayOfWeek = dayOfWeek.plus(1)
                }
            } else {
                while (endPadding > 0) {
                    div(classes = "day ${dayOfWeek.name.lowercase()}") {
                        +" "
                    }
                    endPadding--
                    dayOfWeek = dayOfWeek.plus(1)
                }
            }
        }
    }
}

fun HTML.calendarTemplate(
    year: CalYear,
    config: CalendarConfig,
) {
    head {
        title { +"Calendar" }
        styleLink("/rsc/interface.css")
        styleLink("/rsc/calendar.css")
        script(src = "/rsc/paged.polyfill.js") {}
        unsafe {
            +"""
                <link rel="preconnect" href="https://fonts.googleapis.com">
                <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                <link href="https://fonts.googleapis.com/css2?family=Dancing+Script:wght@400&family=Noto+Serif&family=Playfair+Display:ital,wght@0,400;0,500;0,600;0,700;0,800;0,900;1,400;1,500;1,600;1,700;1,800;1,900&display=swap" rel="stylesheet">
            """.trimIndent()
        }
    }
    body {
        div(classes = "calendar-cover") {
            style = "background-image: url(\"/cfg/${config.id}/${year.year}/cover.png\")"
        }
        year.forEach { month ->
            div(classes = "month-cover") {
                style = "background-image: url(\"/cfg/${config.id}/${year.year}/months/${month.id}.png\")"
            }
            month(month, "full start-on-${config.startOfWeek.name.lowercase()}", config, true, 5)
        }
    }
}
