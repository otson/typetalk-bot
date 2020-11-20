package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.WebhookMessageDTO
import fi.nuortimo.typetalkbot.dto.WebhookReplyDTO
import fi.nuortimo.typetalkbot.enums.Command
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WebhookService {

    @Autowired
    private lateinit var aniListService: AniListService

    fun processIncomingWebhookMessage(message: WebhookMessageDTO): WebhookReplyDTO? {
        return when (Command.getCommand(message.post.message)) {
            Command.HELLO -> getHelloReply(message)
            Command.TODAY -> getTodayReply(message)
            Command.SUB -> getSubReply(message)
            Command.UNSUPPORTED -> getUnsupportedReply(message)
            else -> null
        }
    }

    private fun getSubReply(message: WebhookMessageDTO) =
            WebhookReplyDTO(aniListService.addSubscription(message), message.post.id)

    private fun getTodayReply(message: WebhookMessageDTO) =
            WebhookReplyDTO(aniListService.getUpcomingAnimeMessage(), message.post.id)

    private fun getUnsupportedReply(message: WebhookMessageDTO) =
            WebhookReplyDTO("Unsupported command.", message.post.id)

    private fun getHelloReply(message: WebhookMessageDTO) =
            WebhookReplyDTO("Hi ${message.post.account.name}!", message.post.id)
}