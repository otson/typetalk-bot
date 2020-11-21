package fi.nuortimo.typetalkbot

import fi.nuortimo.typetalkbot.dto.typetalk.AccountDTO
import fi.nuortimo.typetalkbot.dto.typetalk.PostDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkMessageDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkResponseDTO
import fi.nuortimo.typetalkbot.service.AniListService
import fi.nuortimo.typetalkbot.service.WebhookService
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Webhook")
class WebhookIT : IT {

    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    @InjectMocks
    private lateinit var webhookService: WebhookService

    @Mock
    private lateinit var aniListService: AniListService

    @Test
    @DisplayName("Hello commands returns hello")
    fun helloCommandReturnsHello() {
        val expectedName = "Name"
        val response = restTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "!hello", account = AccountDTO(name = expectedName)))), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, containsString("Hi $expectedName"))
    }

    @Test
    @DisplayName("Unsupported command returns unsupported")
    fun unsupportedCommandReturnsUnsupported() {
        val response = restTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "!123"))), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, containsString("Unsupported"))
    }

    @Test
    @DisplayName("Not command returns empty body")
    fun notCommandReturnsEmptyBody() {
        val response = restTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "Hello"))), String::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body, nullValue())
    }

    @Test
    @DisplayName("Backlog webhook endpoint responds with OK")
    fun backLogWebhookEndpointRespondsWithOk() {
        val response = restTemplate.postForEntity("http://localhost:$port/webhook/backlog",
                HttpEntity("backlogMessage"), String::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
    }

    @Test
    @DisplayName("Today command returns upcoming anime")
    fun todayCommandReturnsUpcomingAnime() {
        val expectedMessage = "message"
        MockitoAnnotations.openMocks(this)
        `when`(aniListService.getUpcomingAnimeMessage()).thenReturn(expectedMessage)
        val response = restTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "!today"))), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, equalTo(expectedMessage))
    }

    @Test
    @DisplayName("Sub command responds with subscribed")
    fun subCommandRespondsWithSubscribed() {
        val expectedMessage = "Subscribed message"
        val typetalkMessage = TypetalkMessageDTO(PostDTO(message = "!sub 123"))
        MockitoAnnotations.openMocks(this)
        `when`(aniListService.addSubscription(typetalkMessage)).thenReturn(expectedMessage)
        val response = restTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(typetalkMessage), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, equalTo(expectedMessage))
    }
}