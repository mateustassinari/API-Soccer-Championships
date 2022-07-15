package br.com.mateus.projetoRestPuc.rabbit

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class QueueConsumer {
    @RabbitListener(queues = ["\${queue.name}"])
    fun receive(@Payload fileBody: String) {
        println("Message $fileBody")
    }
}