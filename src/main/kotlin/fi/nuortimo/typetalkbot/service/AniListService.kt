package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.WebhookMessageDTO
import fi.nuortimo.typetalkbot.dto.anilist.AniListRequestDTO
import fi.nuortimo.typetalkbot.dto.anilist.AniListResponseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.annotation.PostConstruct
import kotlin.streams.toList

@Service
class AniListService {

    @Autowired
    private lateinit var restTemplate: RestTemplate
    private val headers = HttpHeaders()

    @Autowired
    private lateinit var mediaSubscriptionService: MediaSubscriptionService

    @PostConstruct
    fun init() {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
    }

    fun getUpcomingAnimeMessage(): String {
        val upcomingAnime = getUpcomingAnime()
        var response = "これから放送されるアニメ："
        val medias = upcomingAnime.data.page.media.sortedBy { it.nextAiringEpisode.timeUntilAiring }.take(10)
        for (media in medias) {
            response += "\n${convertSecondsToRelative(media.nextAiringEpisode.timeUntilAiring)}  \t${media.title.native}  \t第${media.nextAiringEpisode.episode}話"
        }
        return response
    }

    fun addSubscription(message: WebhookMessageDTO): String {
        return try {
            val mediaId = message.post.message.drop(5).split(" ").first().toInt()
            val userId = message.post.account.id
            mediaSubscriptionService.addSubscription(mediaId, userId)
            "Subcribed!"
        } catch (e: Exception) {
            "Invalid subscription value, should be numerical id of the media."
        }
    }

    private fun getUpcomingAnime(): AniListResponseDTO {
        val mediaIds = getUpcomingMediaIds()
        return getAnimeByMediaIds(mediaIds)
    }

    private fun getAnimeByMediaIds(mediaIds: List<Int>): AniListResponseDTO {
        val params = mapOf("id_in" to mediaIds, "isAdult" to false, "type" to "ANIME", "countryOfOrigin" to "JP", "tag_not_in" to "Kids")
        return queryAniList(MEDIA_BY_IDS_QUERY, params)
    }

    private fun getUpcomingMediaIds(): List<Int> {
        val now = System.currentTimeMillis() / 1000
        val params = mapOf("airingAt_greater" to now.toString(), "airingAt_lesser" to (now + 3600 * 24).toString())
        return queryAniList(UPCOMING_MEDIA_IDS_QUERY, params).data.page.airingSchedules.stream().map { it.mediaId }.toList()
    }

    private fun queryAniList(query: String, params: Map<*, *>): AniListResponseDTO {
        val response = restTemplate.postForEntity(ANILIST_URL, HttpEntity(AniListRequestDTO(query, params), headers), AniListResponseDTO::class.java)
        return response.body ?: AniListResponseDTO()
    }

    private fun convertSecondsToRelative(seconds: Int): String {
        var s = seconds
        val hours = s / 3600
        s %= 3600
        val minutes: Int = s / 60
        s %= 60
        var response = ""
        if (hours != 0) response += "${hours}時間"
        if (minutes != 0) response += "${minutes}分"
        if (seconds != 0 && response == "") response += "${s}秒"
        response += "後"
        return response
    }

    companion object {
        const val ANILIST_URL = "https://graphql.anilist.co"
        var UPCOMING_MEDIA_IDS_QUERY: String =
                "query (\$airingAt_lesser: Int, \$airingAt_greater: Int) {" +
                        "    Page (page: 1, perPage: 100) {" +
                        "        airingSchedules (airingAt_lesser: \$airingAt_lesser, airingAt_greater: \$airingAt_greater) {" +
                        "            mediaId" +
                        "        }" +
                        "    }" +
                        "}"
        var MEDIA_BY_IDS_QUERY: String =
                "query (\$countryOfOrigin: CountryCode, \$id_in: [Int], \$type: MediaType," +
                        "\$isAdult: Boolean, \$tag_not_in: [String]) {" +
                        "	Page (page: 1, perPage: 100) {" +
                        "		pageInfo {" +
                        "			total" +
                        "		}" +
                        "			media (countryOfOrigin: \$countryOfOrigin, id_in: \$id_in, type: \$type," +
                        "              isAdult: \$isAdult, tag_not_in: \$tag_not_in) {" +
                        "			id" +
                        "			title {" +
                        "				english" +
                        "				native" +
                        "			}" +
                        "           nextAiringEpisode {" +
                        "               timeUntilAiring" +
                        "               episode" +
                        "           }" +
                        "		}" +
                        "	}" +
                        "}"
    }
}