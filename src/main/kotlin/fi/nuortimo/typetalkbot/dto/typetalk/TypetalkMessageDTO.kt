package fi.nuortimo.typetalkbot.dto.typetalk

data class TypetalkMessageDTO(
        var post: Post = Post()
)

data class Post(
        var message: String = "",
        var id: Int = 0,
        var account: Account = Account()
)

data class Account(
        var id: Int = 0,
        var name: String = ""
)
