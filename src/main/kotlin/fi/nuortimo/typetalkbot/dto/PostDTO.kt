package fi.nuortimo.typetalkbot.dto

data class PostDTO(
        var topicId : Int = 0,
        var message: String = "",
        var id: Int = 0,
        var account: AccountDTO = AccountDTO()
)
