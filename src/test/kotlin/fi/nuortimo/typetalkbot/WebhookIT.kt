package fi.nuortimo.typetalkbot

import fi.nuortimo.typetalkbot.dto.backlog.BacklogRequestDTO
import fi.nuortimo.typetalkbot.dto.typetalk.AccountDTO
import fi.nuortimo.typetalkbot.dto.typetalk.PostDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkMessageDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkResponseDTO
import fi.nuortimo.typetalkbot.repository.MediaSubscriptionRepository
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Webhook")
class WebhookIT : IT {

    private val testRestTemplate = TestRestTemplate()

    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var mediaSubscriptionRepository: MediaSubscriptionRepository

    @Test
    @DisplayName("Hello commands returns hello")
    fun helloCommandReturnsHello() {
        val expectedName = "Name"
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "!hello", account = AccountDTO(name = expectedName)))), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, containsString("Hi $expectedName"))
    }

    @Test
    @DisplayName("Unsupported command returns unsupported")
    fun unsupportedCommandReturnsUnsupported() {
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "!123"))), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, containsString("Unsupported"))
    }

    @Test
    @DisplayName("Not command returns empty body")
    fun notCommandReturnsEmptyBody() {
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(PostDTO(message = "Hello"))), String::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body, nullValue())
    }

    @Test
    @DisplayName("Sub command responds with subscribed")
    fun subCommandRespondsWithSubscribed() {
        val expectedMessage = "Subscribed!"
        val typetalkMessage = TypetalkMessageDTO(PostDTO(message = "!sub 123"))
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(typetalkMessage), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, equalTo(expectedMessage))
    }

    @Test
    @DisplayName("Backlog webhook endpoint responds with OK")
    fun backLogWebhookEndpointRespondsWithOk() {
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/backlog",
                HttpEntity(BacklogRequestDTO()), String::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
    }

    @Test
    @DisplayName("Unsub command responds with unsubscribed")
    fun unsubCommandRespondsWithUnsubscribed() {
        val username = "user"
        val mediaId = 1
        val expectedMessage = "Subscription removed!"
        mediaSubscriptionRepository.save(MediaSubscription(username = username, mediaId = mediaId))
        val typetalkMessage = TypetalkMessageDTO(PostDTO(message = "!unsub $mediaId", account = AccountDTO(name = username)))
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(typetalkMessage), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, equalTo(expectedMessage))
    }

    @Test
    @DisplayName("Listsubs command responds with subscriptions")
    fun listSubsCommandRespondsWithSubscriptions() {
        val username = "user"
        val mediaId = 321
        mediaSubscriptionRepository.save(MediaSubscription(username = username, mediaId = mediaId))
        val typetalkMessage = TypetalkMessageDTO(PostDTO(message = "!listsubs", account = AccountDTO(name = username)))
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(typetalkMessage), TypetalkResponseDTO::class.java)
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body?.message, containsString(mediaId.toString()))
    }
}