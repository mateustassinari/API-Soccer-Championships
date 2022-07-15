package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.rabbit.QueueSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/events")
class EventController() {

    @Autowired
    private val queueSender: QueueSender? = null

    @GetMapping
    fun send(): String? {
        queueSender?.send("test message")
        return "ok. done"
    }

}