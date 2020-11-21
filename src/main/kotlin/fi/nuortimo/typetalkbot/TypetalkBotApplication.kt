package fi.nuortimo.typetalkbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TypetalkBotApplication

fun main(args: Array<String>) {
    runApplication<TypetalkBotApplication>(*args)
}
