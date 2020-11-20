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
    fun addSubscription(username: String, mediaId: Int, topicId: Int): Boolean {
        return if (!mediaSubscriptionRepository.existsByUsernameEqualsAndMediaIdEquals(username, mediaId)) {
            val sub = MediaSubscription(username = username, mediaId = mediaId, topicId = topicId)
            mediaSubscriptionRepository.save(sub)
            true
        } else false
    }
}