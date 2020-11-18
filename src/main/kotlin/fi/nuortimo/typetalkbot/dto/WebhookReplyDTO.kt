package fi.nuortimo.typetalkbot.dto

data class WebhookReplyDTO(
        var message: String,
        var replyTo: Int
)