package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.anilist.AniListRequestDTO
import fi.nuortimo.typetalkbot.dto.anilist.AniListResponseDTO
import fi.nuortimo.typetalkbot.dto.typetalk.TypetalkMessageDTO
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

    @Autowired
    private lateinit var typeTalkService: TypetalkService

    @PostConstruct
    fun init() {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
    }

    @Scheduled(fixedRate = 1000 * 60)
    fun processSubscriptions() {
        val subs = mediaSubscriptionRepository.findAll()
        val mediaIdsToNotify = getUpcomingMediaIds(1, 2)
        val subsMediaIdsToNotify = subs.map { it.mediaId }.toSet().filter { mediaIdsToNotify.contains(it) }.toSet()
        val idNameMap = getAnimeByMediaIds(subsMediaIdsToNotify).data.page.media
                .map {
                    it.id to (it.title.english ?: it.title.native)
                }.toMap()
        val subsToNotify = mediaSubscriptionRepository.findByMediaIdIn(subsMediaIdsToNotify)
        for (sub in subsToNotify) {
            val msg = "${sub.username}, ${idNameMap[sub.mediaId]} airs soon!"
            typeTalkService.sendMessage(sub.topicId, msg)
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

    fun addSubscription(message: TypetalkMessageDTO): String {
        return try {
            val username = message.post.account.name
            val mediaId = message.post.message.drop(5).toInt()
            val topicId = message.post.topicId
            return if (mediaSubscriptionService.addSubscription(username, mediaId, topicId)) {
                "Subscribed!"
            } else "Subscription already exists!"
        } catch (e: Exception) {
            "Invalid subscription value, should be numerical id of the media."
        }
    }

    fun removeSubscription(message: TypetalkMessageDTO): String {
        return try {
            val username = message.post.account.name
            val mediaId = message.post.message.drop(7).toInt()
            return if (mediaSubscriptionService.removeSubscription(username, mediaId)) {
                "Subscription removed!"
            } else "Subscription does not exist!"
        } catch (e: Exception) {
            "Invalid subscription value, should be numerical id of the media."
        }
    }

    fun listSubscriptions(message: TypetalkMessageDTO): String {
        val subs = mediaSubscriptionService.findSubscriptions(message.post.account.name)
        return if (subs.isEmpty()) "You have no subscriptions."
        else "You are currently subscribed to anime with the following ids: ${subs.map { it.mediaId }.joinToString()}"
    }

    private fun getUpcomingAnime(): AniListResponseDTO {
        val mediaIds = getUpcomingMediaIds(0, 24 * 60)
        return getAnimeByMediaIds(mediaIds)
    }

    private fun getAnimeByMediaIds(mediaIds: Set<Int>): AniListResponseDTO {
        val params = mapOf("id_in" to mediaIds, "isAdult" to false, "type" to "ANIME", "countryOfOrigin" to "JP", "tag_not_in" to "Kids")
        return queryAniList(MEDIA_BY_IDS_QUERY, params)
    }

    private fun getUpcomingMediaIds(minMinutesUntilAiring: Int, maxMinutesUntilAiring: Int): Set<Int> {
        val now = System.currentTimeMillis() / 1000
        val params = mapOf("airingAt_greater" to (now + 60 * minMinutesUntilAiring).toString(), "airingAt_lesser" to (now + 60 * maxMinutesUntilAiring).toString())
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