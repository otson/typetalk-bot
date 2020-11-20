package fi.nuortimo.typetalkbot.service

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.annotation.PostConstruct

@Service
class TypetalkService {

    private val logger: Logger = LogManager.getLogger(TypetalkService::class.java)

    @Value("\${TYPETALK_TOKEN:#{null}}")
    private var typetalkToken: String? = null

    @Autowired
    private lateinit var restTemplate: RestTemplate
    private val httpHeaders = HttpHeaders()

    @PostConstruct
    fun init() {
        httpHeaders.set(TYPETALK_TOKEN_HEADER, typetalkToken)
    }

    fun sendMessage(topicId: Int, message: String) {
        if (typetalkToken == null) {
            logger.info("TYPETALK_TOKEN has not been set, cannot send message to TypeTalk.")
        }
        try {
            restTemplate.postForEntity("$API_URL/v1/topics/$topicId", HttpEntity(mapOf("message" to message), httpHeaders), Void::class.java)
        } catch (e: Exception) {
            logger.info("Failed to send message to Typetalk. Error message: {}", e.message)
        }
    }

    companion object {
        const val TYPETALK_TOKEN_HEADER = "X-Typetalk-Token"
        const val API_URL = "https://typetalk.com/api"

    }
}