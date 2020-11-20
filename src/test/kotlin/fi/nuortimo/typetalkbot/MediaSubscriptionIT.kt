package fi.nuortimo.typetalkbot

import fi.nuortimo.typetalkbot.repository.MediaSubscriptionRepository
import fi.nuortimo.typetalkbot.service.MediaSubscriptionService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayName("MediaSubscription")
class MediaSubscriptionIT {

    @Autowired
    private lateinit var mediaSubscriptionService: MediaSubscriptionService

    @Autowired
    private lateinit var mediaSubscriptionRepository: MediaSubscriptionRepository

    @Test
    @DisplayName("Saves mediaSubscription")
    fun saveMediaSubscription(){
        mediaSubscriptionService.addSubscription(1, 1)
        assertThat(mediaSubscriptionRepository.count(), equalTo(1L))
    }

    @Test
    @DisplayName("Does not save duplicate mediaSubscription")
    fun doesNotSaveDuplicateMediaSubscription(){
        val userId  = 1
        val mediaId = 1
        assertThat(mediaSubscriptionService.addSubscription(userId, mediaId), equalTo(true))
        assertThat(mediaSubscriptionService.addSubscription(userId, mediaId), equalTo(false))
        assertThat(mediaSubscriptionRepository.count(), equalTo(1L))
    }
}