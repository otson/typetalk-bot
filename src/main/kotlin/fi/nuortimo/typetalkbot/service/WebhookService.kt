package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkMessageDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkResponseDTO
import fi.nuortimo.typetalkbot.enums.Command
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WebhookService {

    @Autowired
    private lateinit var aniListService: AniListService

    fun processTypetalkMessage(message: TypetalkMessageDTO): TypetalkResponseDTO? {
        return when (Command.getCommand(message.post.message)) {
            Command.HELLO -> getHelloReply(message)
            Command.TODAY -> getTodayReply(message)
            Command.SUB -> getSubReply(message)
            Command.UNSUPPORTED -> getUnsupportedReply(message)
            else -> null
        }
    }

    private fun getSubReply(message: TypetalkMessageDTO) =
            TypetalkResponseDTO(aniListService.addSubscription(message), message.post.id)

    private fun getTodayReply(message: TypetalkMessageDTO) =
            TypetalkResponseDTO(aniListService.getUpcomingAnimeMessage(), message.post.id)

    private fun getUnsupportedReply(message: TypetalkMessageDTO) =
            TypetalkResponseDTO("Unsupported command.", message.post.id)

    private fun getHelloReply(message: TypetalkMessageDTO) =
            TypetalkResponseDTO("Hi ${message.post.account.name}!", message.post.id)
}