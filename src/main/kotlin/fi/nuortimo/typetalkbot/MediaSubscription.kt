package fi.nuortimo.typetalkbot

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class MediaSubscription(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null,
        val username: String,
        val mediaId: Int,
        val topicId: Int
)