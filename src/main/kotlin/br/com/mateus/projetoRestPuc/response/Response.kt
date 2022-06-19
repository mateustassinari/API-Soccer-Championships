package br.com.mateus.projetoRestPuc.response

data class Response<T> (
    var erros: ArrayList<String> = arrayListOf(),
    var data: T? = null
)