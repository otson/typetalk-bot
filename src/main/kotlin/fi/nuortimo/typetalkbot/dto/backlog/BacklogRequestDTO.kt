package fi.nuortimo.typetalkbot.dto.backlog

data class BacklogRequestDTO(
        var project: Project = Project(),
        var type : Int = 0,
        var content: Content = Content(),
        var createdUser: CreatedUser = CreatedUser()
)

data class Project(
        var name: String = ""
)

data class Content(
        var summary: String = ""
)

data class CreatedUser(
        var name: String = ""
)