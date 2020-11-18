package fi.nuortimo.typetalkbot.dto

data class PostDTO(
        var message: String? = null,
        var id: Int = 0,
        var account: AccountDTO = AccountDTO()
)
