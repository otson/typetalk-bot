package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.WebhookMessageDTO
import fi.nuortimo.typetalkbot.dto.anilist.AniListRequestDTO
import fi.nuortimo.typetalkbot.dto.anilist.AniListResponseDTO
import fi.nuortimo.typetalkbot.repository.MediaSubscriptionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
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

    @Autowired
    private lateinit var mediaSubscriptionRepository: MediaSubscriptionRepository

    @PostConstruct
    fun init() {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
    }

    @Scheduled(fixedRate = 5000)
    fun processSubscriptions() {
        val subs = mediaSubscriptionRepository.findAll()
        val mediaIdsToNotify = getUpcomingMediaIds(1)
        val subsMediaIdsToNotify = subs.map { it.mediaId }.toSet().filter { mediaIdsToNotify.contains(it) }.toSet()
        val idNameMap = getAnimeByMediaIds(subsMediaIdsToNotify).data.page.media.map { it.id to it.title.english }.toMap()
        val subsToNotify = mediaSubscriptionRepository.findByMediaIdIn(subsMediaIdsToNotify)
        // TODO: Send messages to TypeTalk
        for (sub in subsToNotify) {
            println("User ${sub.userId}, ${idNameMap[sub.mediaId]} airs soon!")
        }
    }
    
    fun getUpcomingAnimeMessage(): String {
        val upcomingAnime = getUpcomingAnime()
        var response = "Anime airing in the next 24 hours："
        val medias = upcomingAnime.data.page.media.sortedBy { it.nextAiringEpisode?.timeUntilAiring }.take(20)
        for (media in medias) {
            if (media.nextAiringEpisode != null) {
                response += "\n${convertSecondsToRelative(media.nextAiringEpisode.timeUntilAiring)}: \tEpisode ${media.nextAiringEpisode.episode} of " +
                        "[${media.title.native}](${ANILIST_URL}${media.id}) "
                if (media.title.english != null) response += "(${media.title.english}) "
                response += "(sub id: ${media.id})"
            }
        }
        return response
    }

    fun addSubscription(message: WebhookMessageDTO): String {
        return try {
            val userId = message.post.account.id
            val mediaId = message.post.message.drop(5).toInt()
            return if (mediaSubscriptionService.addSubscription(userId, mediaId)) {
                "Subscribed!"
            } else "Subscription already exists!"
        } catch (e: Exception) {
            "Invalid subscription value, should be numerical id of the media."
        }
    }

    private fun getUpcomingAnime(): AniListResponseDTO {
        val mediaIds = getUpcomingMediaIds(24)
        return getAnimeByMediaIds(mediaIds)
    }

    private fun getAnimeByMediaIds(mediaIds: Set<Int>): AniListResponseDTO {
        val params = mapOf("id_in" to mediaIds, "isAdult" to false, "type" to "ANIME", "countryOfOrigin" to "JP", "tag_not_in" to "Kids")
        return queryAniList(MEDIA_BY_IDS_QUERY, params)
    }

    private fun getUpcomingMediaIds(maxHoursUntilAiring: Int): Set<Int> {
        val now = System.currentTimeMillis() / 1000
        val params = mapOf("airingAt_greater" to now.toString(), "airingAt_lesser" to (now + 3600 * maxHoursUntilAiring).toString())
        return queryAniList(UPCOMING_MEDIA_IDS_QUERY, params).data.page.airingSchedules.stream().map { it.mediaId }.toList().toSet()
    }

    private fun queryAniList(query: String, params: Map<*, *>): AniListResponseDTO {
        val response = restTemplate.postForEntity(ANILIST_API_URL, HttpEntity(AniListRequestDTO(query, params), headers), AniListResponseDTO::class.java)
        return response.body ?: AniListResponseDTO()
    }

    private fun convertSecondsToRelative(seconds: Int): String {
        var s = seconds
        val hours = s / 3600
        s %= 3600
        val minutes: Int = s / 60
        s %= 60
        var response = "In "
        if (hours != 0) response += "${hours}h "
        if (minutes != 0) response += "${minutes}m "
        if (seconds != 0 && response == "In ") response += "${s}s"
        return response.trimEnd()
    }

    companion object {
        const val ANILIST_URL = "https://anilist.co/anime/"
        const val ANILIST_API_URL = "https://graphql.anilist.co"
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