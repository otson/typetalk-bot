package fi.nuortimo.typetalkbot.dto.anilist

import com.fasterxml.jackson.annotation.JsonProperty

data class AniListResponseDTO(
        val data: Data = Data()
)

data class Data(
        @field:JsonProperty("Page")
        val page: Page = Page()
)

data class Page(
        val airingSchedules: List<AiringSchedulesItem> = emptyList(),
        val media: List<MediaItem> = emptyList()
)

data class AiringSchedulesItem(
        val mediaId: Int
)

data class MediaItem(
        val title: Title = Title(),
        val nextAiringEpisode : NextAiringEpisode = NextAiringEpisode()
)

data class NextAiringEpisode (
        val timeUntilAiring : Int = -1,
        val episode : Int = -1
)

data class Title(
        val english: String? = null,
        val native: String? = null
)