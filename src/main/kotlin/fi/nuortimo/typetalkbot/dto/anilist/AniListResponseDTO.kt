package fi.nuortimo.typetalkbot.dto.anilist

import com.fasterxml.jackson.annotation.JsonProperty

data class AniListResponseDTO(
        val data: Data = Data()
)

data class AiringSchedulesItem(
        val mediaId: Int
)

data class Data(
        @field:JsonProperty("Page")
        val page: Page = Page()
)

data class Page(
        val airingSchedules: List<AiringSchedulesItem> = emptyList()
)