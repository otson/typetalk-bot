package fi.nuortimo.typetalkbot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate




@Configuration
class BeanConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}