package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.backlog.BacklogMessageDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BacklogService {

    @Autowired
    private lateinit var typetalkService: TypetalkService

    fun processIssueCreatedMessage(message: BacklogMessageDTO) {
        val comment = "${message.createdUser.name} created a new issue " +
                "${message.content.summary} to project ${message.project.name}."
        typetalkService.sendMessage(comment)
    }
}