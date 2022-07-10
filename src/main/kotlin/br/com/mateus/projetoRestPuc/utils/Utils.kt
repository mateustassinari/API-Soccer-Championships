package br.com.mateus.projetoRestPuc.utils

import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class Utils {

    fun parseDate(dateString: String) : Date? {

        var date : Date? = null
        try {
            val format = SimpleDateFormat("dd/MM/yyyy")
            format.isLenient = false
            date = format.parse(dateString)
        } catch (e: Exception) {
            e.stackTrace
        }
        return date

    }

}