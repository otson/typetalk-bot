package fi.nuortimo.typetalkbot.controller

import fi.nuortimo.typetalkbot.dto.backlog.BacklogMessageDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkMessageDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkResponseDTO
import fi.nuortimo.typetalkbot.service.WebhookService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController {

    @Autowired
    private lateinit var webhookService: WebhookService
    private val logger: Logger = LogManager.getLogger(WebhookController::class.java)

    @PostMapping("/typetalk")
    fun receiveFromTypetalk(@RequestBody message: TypetalkMessageDTO): ResponseEntity<TypetalkResponseDTO?> {
        logger.info("Received Typetalk message: $message")
        return ResponseEntity.ok().body(webhookService.processTypetalkMessage(message))
    }

    @PostMapping("/backlog")
    fun receiveFromBacklog(@RequestBody message: BacklogMessageDTO): ResponseEntity<Nothing> {
        logger.info("Received Backlog message: $message")
        webhookService.processBacklogMessage(message)
        return ResponseEntity.ok().build()
    }
}