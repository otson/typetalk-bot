package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.WebhookMessageDTO
import fi.nuortimo.typetalkbot.dto.WebhookReplyDTO
import fi.nuortimo.typetalkbot.enums.Command
import org.springframework.stereotype.Service

@Service
class WebhookService {

    fun processIncomingWebhookMessage(message: WebhookMessageDTO): WebhookReplyDTO? {
        return when (Command.getCommand(message.post.message)) {
            Command.HELLO -> getHelloReply(message)
            Command.UNSUPPORTED -> getUnsupportedReply(message)
            else -> null
        }
    }

    private fun getUnsupportedReply(message: WebhookMessageDTO) =
            WebhookReplyDTO("Unsupported command.", message.post.id)

    private fun getHelloReply(message: WebhookMessageDTO) =
            WebhookReplyDTO("Hi ${message.post.account.name}!", message.post.id)
}