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
        mediaSubscriptionService.addSubscription("name", 1,1)
        assertThat(mediaSubscriptionRepository.count(), equalTo(1L))
    }

    @Test
    @DisplayName("Does not save duplicate mediaSubscription")
    fun doesNotSaveDuplicateMediaSubscription(){
        val username = "name"
        val mediaId = 1
        assertThat(mediaSubscriptionService.addSubscription(username, mediaId, 1), equalTo(true))
        assertThat(mediaSubscriptionService.addSubscription(username, mediaId, 2), equalTo(false))
        assertThat(mediaSubscriptionRepository.count(), equalTo(1L))
    }
}