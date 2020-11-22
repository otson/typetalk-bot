package fi.nuortimo.typetalkbot.IT

import fi.nuortimo.typetalkbot.service.TypetalkService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest
@DisplayName("Typetalk")
class TypeTalkIT : IT {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var typetalkService: TypetalkService

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun init() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    @DisplayName("Sends comment")
    fun sendsComment() {
        val expectedMessage = "Hi"
        typetalkService.typetalkApiUrl = "http://localhost"
        typetalkService.typetalkToken = "token"
        mockServer.expect(ExpectedCount.once(),
                requestTo(URI("${typetalkService.typetalkApiUrl}")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"message\": $expectedMessage}"))
        typetalkService.sendMessage(expectedMessage)
        mockServer.verify()
    }

    @Test
    @DisplayName("Does not send comment without API token")
    fun doesNotSendCommentWithoutApiToken() {
        typetalkService.typetalkToken = null
        mockServer.expect(ExpectedCount.never(),
                requestTo(URI("${typetalkService.typetalkApiUrl}")))
        typetalkService.sendMessage("message")
        mockServer.verify()
        typetalkService.typetalkToken = "token"
    }
}