package fi.nuortimo.typetalkbot.repository

import fi.nuortimo.typetalkbot.MediaSubscription
import org.springframework.data.jpa.repository.JpaRepository

interface MediaSubscriptionRepository : JpaRepository<MediaSubscription, Int>{
    fun existsByUserIdEqualsAndMediaIdEquals(userId : Int, mediaId: Int) : Boolean
}