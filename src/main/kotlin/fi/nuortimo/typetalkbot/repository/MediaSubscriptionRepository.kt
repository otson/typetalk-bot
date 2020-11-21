package fi.nuortimo.typetalkbot.repository

import fi.nuortimo.typetalkbot.MediaSubscription
import org.springframework.data.jpa.repository.JpaRepository

interface MediaSubscriptionRepository : JpaRepository<MediaSubscription, Int>{
    fun existsByUsernameAndMediaId(username : String, mediaId: Int) : Boolean
    fun findByMediaIdIn(subsMediaIds: Set<Int>): Set<MediaSubscription>
    fun deleteByUsernameAndMediaId(username : String, mediaId: Int)
    fun findByUsername(username : String) : List<MediaSubscription>
}