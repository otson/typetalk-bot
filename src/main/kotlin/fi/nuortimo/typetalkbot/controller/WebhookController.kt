package fi.nuortimo.typetalkbot.controller

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class WebhookController {

    private val logger: Logger = LogManager.getLogger(WebhookController::class.java)

    @PostMapping
    fun receiveWebhook(@RequestBody message: String): ResponseEntity<Any> {
        logger.info("Received webhook message: $message")
        return ResponseEntity.ok().build()
    }
}