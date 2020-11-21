package fi.nuortimo.typetalkbot

import fi.nuortimo.typetalkbot.service.TypetalkService
import org.junit.jupiter.api.BeforeEach
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
class TypeTalkIT {

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
    fun sendsComment() {
        val expectedTopicId = 1
        val expectedMessage = "Hi"
        mockServer.expect(ExpectedCount.once(),
                requestTo(URI("${TypetalkService.API_URL}/v1/topics/$expectedTopicId")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"message\": $expectedMessage}"))
        typetalkService.sendMessage(expectedTopicId, expectedMessage)
        mockServer.verify()
    }

    @Test
    fun doesNotSendCommentWithoutApiToken() {
        val expectedTopicId = 1
        typetalkService.typetalkToken = null
        mockServer.expect(ExpectedCount.never(),
                requestTo(URI("${TypetalkService.API_URL}/v1/topics/$expectedTopicId")))
        typetalkService.sendMessage(expectedTopicId, "message")
        mockServer.verify()
        typetalkService.typetalkToken = "token"
    }
}