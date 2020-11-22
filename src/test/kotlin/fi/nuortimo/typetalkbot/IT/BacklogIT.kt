package fi.nuortimo.typetalkbot.IT

import fi.nuortimo.typetalkbot.dto.backlog.BacklogMessageDTO
import fi.nuortimo.typetalkbot.dto.backlog.Content
import fi.nuortimo.typetalkbot.dto.backlog.CreatedUser
import fi.nuortimo.typetalkbot.dto.backlog.Project
import fi.nuortimo.typetalkbot.service.TypetalkService
import fi.nuortimo.typetalkbot.service.WebhookService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.web.client.RestTemplate
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Backlog")
class BacklogIT : IT {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var webhookService: WebhookService

    @Autowired
    private lateinit var typetalkService: TypetalkService

    @Test
    @DisplayName("Backlog webhook new issue event sends comment")
    fun backLogWebhookNewTaskEventSendsComment() {
        val projectName = "project"
        val issueName = "issue"
        val createdUser = "user"
        val expectedMessage = "$createdUser created a new issue $issueName to project $projectName."
        val mockServer = MockRestServiceServer.createServer(restTemplate)
        typetalkService.typetalkApiUrl = "http://localhost"
        typetalkService.typetalkToken = "token"
        mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo(URI("${typetalkService.typetalkApiUrl}")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().json("{\"message\": \"$expectedMessage\"}"))
        webhookService.processBacklogMessage(BacklogMessageDTO(Project("project"), 1, Content("issue"), CreatedUser("user")))
        typetalkService.typetalkApiUrl = null
        typetalkService.typetalkToken = null
        mockServer.verify()
    }
}