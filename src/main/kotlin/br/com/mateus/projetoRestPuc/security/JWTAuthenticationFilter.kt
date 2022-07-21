package br.com.mateus.projetoRestPuc.security

import br.com.mateus.projetoRestPuc.dtos.TokenDto
import br.com.mateus.projetoRestPuc.dtos.CredentialsDto
import br.com.mateus.projetoRestPuc.response.Response
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter: UsernamePasswordAuthenticationFilter {

    private var jwtUtil: JWTUtil

    constructor(authenticationManager: AuthenticationManager, jwtUtil: JWTUtil) : super() {
        this.authenticationManager = authenticationManager
        this.jwtUtil = jwtUtil
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        val (login, password) = ObjectMapper().readValue(request.inputStream, CredentialsDto::class.java)
        try {
            val token = UsernamePasswordAuthenticationToken(login, password)
            return authenticationManager.authenticate(token)
        } catch (e: Exception) {
            throw UsernameNotFoundException("Incorrect Data!")
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse, chain: FilterChain?, authResult: Authentication) {
        val login = (authResult.principal as User).username
        val responsedto: Response<TokenDto> = Response()
        val token = jwtUtil.generateToken(login)
        responsedto.data = TokenDto(token)
        val gson = Gson()
        val json: String = gson.toJson(responsedto)
        response.addHeader("Content-Type","application/json; charset=utf-8")
        response.writer.write(json)
        response.writer.flush()
        response.writer.close()
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, failed: AuthenticationException?) {
        val responsedto: Response<Response<Void>> = Response()
        responsedto.erros.add(failed?.message!!)
        val gson = Gson()
        val json: String = gson.toJson(responsedto)
        response?.addHeader("Content-Type","application/json; charset=utf-8")
        response?.status = HttpServletResponse.SC_NOT_FOUND
        response?.writer?.write(json)
        response?.writer?.flush()
        response?.writer?.close()
    }

}
