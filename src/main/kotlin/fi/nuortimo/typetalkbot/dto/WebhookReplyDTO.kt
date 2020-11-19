package fi.nuortimo.typetalkbot.dto

data class WebhookReplyDTO(
        var message: String? = null,
        var replyTo: Int
)