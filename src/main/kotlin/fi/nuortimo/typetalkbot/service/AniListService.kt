package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.dto.anilist.AniListRequestDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.annotation.PostConstruct

@Service
class AniListService {

    private val restTemplate = RestTemplate()
    private val headers = HttpHeaders()

    @PostConstruct
    fun init() {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
    }

    fun getUpcomingAnime(): String? {
        val now = System.currentTimeMillis() / 1000
        val params = mapOf("airingAt_greater" to now.toString(), "airingAt_lesser" to (now + 3600 * 24).toString())
        return queryAniList(UPCOMING_MEDIA_IDS_QUERY, params)
    }

    private fun queryAniList(query: String, params: Map<String, String>): String? {
        val response = restTemplate.postForEntity(ANILIST_URL, HttpEntity(AniListRequestDTO(query, params), headers), String::class.java)
        println(response.statusCode)
        return response.body
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
    }
}