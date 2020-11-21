package fi.nuortimo.typetalkbot

import com.fasterxml.jackson.databind.ObjectMapper
import fi.nuortimo.typetalkbot.dto.anilist.*
import fi.nuortimo.typetalkbot.service.AniListService
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.RestTemplate
import java.net.URI


@SpringBootTest
@DisplayName("AnimeScheduleService")
class AnimeScheduleServiceIT : IT {

    @Autowired
    private lateinit var aniListService: AniListService

    @Autowired
    private lateinit var restTemplate: RestTemplate

    private lateinit var mockServer: MockRestServiceServer
    private val mapper = ObjectMapper()

    @BeforeEach
    fun init() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    @DisplayName("Today command returns upcoming media")
    fun todayCommandReturnsUpcomingMedia() {
        val expectedTitle = "title"
        val expectedTimeUntil = "1h 1m"
        val expectedEpisodeNumber = "Episode 1 "
        mockServer.expect(ExpectedCount.once(),
                requestTo(URI(AniListService.ANILIST_API_URL)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(AniListResponseDTO(Data(Page((listOf(
                                AiringSchedulesItem(1))))))))
                )
        mockServer.expect(ExpectedCount.once(),
                requestTo(URI(AniListService.ANILIST_API_URL)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(AniListResponseDTO(Data(Page(media = listOf(
                                MediaItem(1, Title("title", "タイトル"),
                                        NextAiringEpisode(3665, 1))))))))
                )

        val upcomingAnime = aniListService.getUpcomingAnimeMessage()

        assertThat(upcomingAnime, containsString(expectedTimeUntil))
        assertThat(upcomingAnime, containsString(expectedEpisodeNumber))
        assertThat(upcomingAnime, containsString(expectedTitle))
        mockServer.verify()
    }
}