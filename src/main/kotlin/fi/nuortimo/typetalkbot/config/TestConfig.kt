package fi.nuortimo.typetalkbot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate


@Configuration
@Profile("test")
class TestConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}