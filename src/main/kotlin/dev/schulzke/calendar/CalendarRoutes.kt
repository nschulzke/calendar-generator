package dev.schulzke

import dev.schulzke.calendar.json
import dev.schulzke.calendar.model.CalYear
import dev.schulzke.calendar.model.CalendarConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import java.io.File

fun Application.calendarRoutes() {
    routing {
        static {
            files("static")
        }

        static("/cfg") {
            files("cfg")
        }

        get("/cal/{config}/{year}") {
            val configPath = call.parameters["config"]!!
            val year = call.parameters["year"]?.toIntOrNull()
            val config = json.decodeFromString<CalendarConfig>(File("cfg/${configPath}/${year}/config.json").readText())
            if (year == null) {
                call.respondText(text = "400: Bad Request", status = HttpStatusCode.BadRequest)
            } else {
                val calYear = CalYear.of(year)
                call.respondHtml { calendarTemplate(calYear, config) }
            }
        }
    }
}
