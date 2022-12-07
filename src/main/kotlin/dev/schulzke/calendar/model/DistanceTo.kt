package dev.schulzke.calendar.model

import kotlinx.datetime.DayOfWeek
import kotlin.math.abs

fun DayOfWeek.distanceTo(other: DayOfWeek): Int {
    return abs(other.value - this.value)
}