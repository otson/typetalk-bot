package fi.nuortimo.typetalkbot.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AnimeScheduleService {

    @Autowired
    private lateinit var aniListService: AniListService

    fun getUpcomingAnime(): String {
        return aniListService.getUpcomingAnimeMessage()
    }
}