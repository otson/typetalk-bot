package fi.nuortimo.typetalkbot.service

import fi.nuortimo.typetalkbot.MediaSubscription
import fi.nuortimo.typetalkbot.repository.MediaSubscriptionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MediaSubscriptionService {

    @Autowired
    private lateinit var mediaSubscriptionRepository: MediaSubscriptionRepository

    @Transactional
    fun addSubscription(userId: Int, mediaId: Int) {
        val sub = MediaSubscription(userId = userId, mediaId = mediaId)
        mediaSubscriptionRepository.save(sub)
        println("Current sub count: " + mediaSubscriptionRepository.count())
    }
}