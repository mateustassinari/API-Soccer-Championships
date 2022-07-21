package br.com.mateus.projetoRestPuc.rabbit

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.core.Queue;

@Configuration
class SenderConfig {

    @Value("\${queue.name}")
    private val queueName: String? = null

    @Bean
    fun queue(): Queue {
        return Queue(queueName, true)
    }
}