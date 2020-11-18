package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.enums.Command
import fi.nuortimo.typetalkbot.dto.WebhookMessageDTO
import fi.nuortimo.typetalkbot.dto.WebhookReplyDTO
import org.springframework.stereotype.Service

@Service
class WebhookService {

    fun processIncomingWebhookMessage(message: WebhookMessageDTO): WebhookReplyDTO? {
        return when (Command.getCommand(message.post.message)) {
            null -> null
            Command.HELLO -> WebhookReplyDTO("Hi ${message.post.account.name}!", message.post.id)
            else -> WebhookReplyDTO("Unsupported command.", message.post.id)
        }
    }
}