package br.com.mateus.projetoRestPuc

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableRabbit
@SpringBootApplication
class ProjetoRestApplication


fun main(args: Array<String>) {
    runApplication<ProjetoRestApplication>(*args)
}

