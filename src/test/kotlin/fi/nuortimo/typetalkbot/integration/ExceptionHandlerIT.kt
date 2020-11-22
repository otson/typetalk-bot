package fi.nuortimo.typetalkbot.integration

import fi.nuortimo.typetalkbot.dto.typetalk.Post
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkMessageDTO
import fi.nuortimo.typetalkbot.service.AniListService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("ExceptionHandler")
class ExceptionHandlerIT : IT {


    private val testRestTemplate = TestRestTemplate()

    @LocalServerPort
    private var port = 0

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Test
    @DisplayName("Handles unexpected response")
    fun handlesUnexpectedResponse() {
        val mockServer = MockRestServiceServer.createServer(restTemplate)
        mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo(URI(AniListService.ANILIST_API_URL)))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("error!"))
        val response = testRestTemplate.postForEntity("http://localhost:$port/webhook/typetalk",
                HttpEntity(TypetalkMessageDTO(Post(message = "!today"))), String::class.java)

        assertThat(response.statusCode, equalTo(HttpStatus.INTERNAL_SERVER_ERROR))
        assertThat(response.body, nullValue())
        mockServer.verify()
    }
}