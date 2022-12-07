package dev.schulzke

import kotlinx.html.*
import java.time.DayOfWeek

fun HtmlBlockTag.month(month: CalMonth, classes: String, startOfWeek: DayOfWeek, minWeeks: Int? = null) {
    div(classes = "month $classes weeks-${month.numberOfWeeks(startOfWeek, minWeeks)}") {
        div(classes = "month-name") {
            +month.name
        }
        div(classes = "month-grid") {
            val dayIter = month.iterator()
            var startPadding = month.initialPaddingSize(startOfWeek)
            var fieldsFilled = 0;
            if (month.previewsAtStart(startOfWeek)) {
                if (month.previousMonth != null) {
                    month(month.previousMonth, "preview", startOfWeek)
                    startPadding--
                    fieldsFilled++
                }
                if (month.nextMonth != null) {
                    month(month.nextMonth, "preview", startOfWeek)
                    startPadding--
                    fieldsFilled++
                }
            }
            while (startPadding > 0) {
                div(classes = "day") {
                    +" "
                }
                fieldsFilled++
                startPadding--
            }
            while (dayIter.hasNext()) {
                val day = dayIter.next()
                div(classes = "day") {
                    +day.date.dayOfMonth.toString()
                }
                fieldsFilled++
            }
            var endPadding = (7 - (fieldsFilled % 7)) % 7
            if (minWeeks != null && month.numberOfWeeks(startOfWeek) < minWeeks) {
                endPadding += 7
            }
            while (endPadding > 2) {
                div(classes = "day") {
                    +" "
                }
                endPadding--
            }
            if (!month.previewsAtStart(startOfWeek)) {
                if (month.previousMonth != null) {
                    month(month.previousMonth, "preview", startOfWeek)
                }
                if (month.nextMonth != null) {
                    month(month.nextMonth, "preview", startOfWeek)
                }
            } else {
                while (endPadding > 0) {
                    div(classes = "day") {
                        +" "
                    }
                    endPadding--
                }
            }
        }
    }
}

fun HTML.calendarTemplate(
    year: CalYear,
    startOfWeek: DayOfWeek,
) {
    head {
        title { +"Calendar" }
        styleLink("/rsc/interface.css")
        styleLink("/rsc/calendar.css")
        script(src = "/rsc/paged.polyfill.js") {}
    }
    body {
        div(classes = "calendar-cover") {

        }
        year.forEach { month ->
            div(classes = "month-cover") {
            }
            month(month, "full", startOfWeek, 5)
        }
    }
}
