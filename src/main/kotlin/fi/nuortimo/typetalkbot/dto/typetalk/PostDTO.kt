package fi.nuortimo.typetalkbot.dto.typetalk

data class PostDTO(
        var message: String = "",
        var id: Int = 0,
        var account: AccountDTO = AccountDTO()
)
