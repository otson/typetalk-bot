package fi.nuortimo.typetalkbot.dto.anilist

data class AniListRequestDTO(
        val query: String? = null,
        val variables: Map<*,*> = emptyMap<String, String>()
)