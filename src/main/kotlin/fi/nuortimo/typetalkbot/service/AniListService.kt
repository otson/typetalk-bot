package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.anilist.AniListRequestDTO
import fi.nuortimo.typetalkbot.dto.anilist.AniListResponseDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.annotation.PostConstruct
import kotlin.streams.toList

@Service
class AniListService {

    private val restTemplate = RestTemplate()
    private val headers = HttpHeaders()

    @PostConstruct
    fun init() {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
    }

    fun getUpcomingAnimeMessage(): String {
        return getUpcomingAnime().toString()
    }

    private fun getUpcomingAnime(): AniListResponseDTO? {
        val mediaIds = getUpcomingMediaIds()

        return getAnimeByMediaIds(mediaIds)
    }

    private fun getAnimeByMediaIds(mediaIds: List<Int>): AniListResponseDTO? {
        val params = mapOf("id_in" to mediaIds)
        val response = restTemplate.postForEntity(ANILIST_URL, HttpEntity(AniListRequestDTO(MEDIA_BY_IDS_QUERY, params), headers), String::class.java)
        return null
    }

    private fun getUpcomingMediaIds() : List<Int> {
        val now = System.currentTimeMillis() / 1000
        val params = mapOf("airingAt_greater" to now.toString(), "airingAt_lesser" to (now + 3600 * 24).toString())
        return queryAniList(UPCOMING_MEDIA_IDS_QUERY, params).data.page.airingSchedules.stream().map { it.mediaId }.toList()
    }

    private fun queryAniList(query: String, params: Map<String, String>): AniListResponseDTO {
        val response = restTemplate.postForEntity(ANILIST_URL, HttpEntity(AniListRequestDTO(query, params), headers), AniListResponseDTO::class.java)
        println(response.statusCode)
        return response.body ?: AniListResponseDTO()
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
            	"query (\$id_in : [Int]) {"+
            	"	Page (page: 1, perPage: 100) {"+
            	"		pageInfo {"+
            	"			total"+
            	"		}"+
            	"		media (id_in: \$id_in) {"+
            	"			id"+
            	"			title {"+
            	"				english"+
            	"				native"+
            	"			}"+
            	"		}"+
            	"	}"+
            	"}"
    }
}