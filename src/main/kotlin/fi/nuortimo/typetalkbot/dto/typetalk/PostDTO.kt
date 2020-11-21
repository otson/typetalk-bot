package fi.nuortimo.typetalkbot.dto.typetalk

import fi.nuortimo.typetalkbot.dto.typetalk.AccountDTO

data class PostDTO(
        var topicId : Int = 0,
        var message: String = "",
        var id: Int = 0,
        var account: AccountDTO = AccountDTO()
)
