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
    var typetalkToken: String? = null

    @Value("\${TYPETALK_API_URL:#{null}}")
    var typetalkApiUrl: String? = null

    @Autowired
    private lateinit var restTemplate: RestTemplate
    private val httpHeaders = HttpHeaders()

    @PostConstruct
    fun init() {
        httpHeaders.set(TYPETALK_TOKEN_HEADER, typetalkToken)
    }

    fun sendMessage(message: String) {
        when {
            typetalkToken == null -> {
                logger.info("TYPETALK_TOKEN environment property has not been set, cannot send message to TypeTalk.")
            }
            typetalkApiUrl == null -> {
                logger.info("TYPETALK_API_URL environment property has not been set, cannot send message to TypeTalk.")
            }
            else -> try {
                restTemplate.postForEntity(typetalkApiUrl!!, HttpEntity(mapOf("message" to message), httpHeaders), Void::class.java)
            } catch (e: Exception) {
                logger.info("Failed to send message to Typetalk. Error message: {}", e.message)
            }
        }
    }

    companion object {
        const val TYPETALK_TOKEN_HEADER = "X-Typetalk-Token"
    }
}