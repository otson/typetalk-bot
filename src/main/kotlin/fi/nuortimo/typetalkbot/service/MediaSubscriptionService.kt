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
    fun addSubscription(username: String, mediaId: Int): Boolean {
        return if (!mediaSubscriptionRepository.existsByUsernameAndMediaId(username, mediaId)) {
            val sub = MediaSubscription(username = username, mediaId = mediaId)
            mediaSubscriptionRepository.save(sub)
            true
        } else false
    }

    @Transactional
    fun removeSubscription(username: String, mediaId: Int): Boolean {
        return if (mediaSubscriptionRepository.existsByUsernameAndMediaId(username, mediaId)) {
            mediaSubscriptionRepository.deleteByUsernameAndMediaId(username, mediaId)
            true
        } else false
    }

    @Transactional(readOnly = true)
    fun findSubscriptions(username: String): List<MediaSubscription> {
        return mediaSubscriptionRepository.findByUsername(username)
    }
}