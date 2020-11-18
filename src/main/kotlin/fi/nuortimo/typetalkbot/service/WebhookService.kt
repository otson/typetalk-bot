package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.WebhookMessageDTO
import fi.nuortimo.typetalkbot.dto.WebhookReplyDTO
import org.springframework.stereotype.Service

@Service
class WebhookService {

    fun processIncomingWebhookMessage(message : WebhookMessageDTO) : WebhookReplyDTO{
        return WebhookReplyDTO("Hi ${message.post.account.name}!", message.post.id)
    }
}