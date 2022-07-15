package br.com.mateus.projetoRestPuc.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter : BasicAuthenticationFilter {

    private var jwtUtil: JWTUtil
    private var userDetailService: UserDetailsService

    constructor(authenticationManager: AuthenticationManager, jwtUtil: JWTUtil, userDetailService: UserDetailsService) : super(authenticationManager) {
        this.jwtUtil = jwtUtil
        this.userDetailService = userDetailService
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader("Authorization")

        val auth = getAuthentication(authorizationHeader)
        if(auth != null) {
            SecurityContextHolder.getContext().authentication = auth
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(authorizationHeader: String?): UsernamePasswordAuthenticationToken? {
        val token = authorizationHeader?.substring(7) ?: ""
        if(jwtUtil.isTokenValid(token)) {
            val username = jwtUtil.getUserName(token)
            val user = userDetailService.loadUserByUsername(username)
            return UsernamePasswordAuthenticationToken(user, null, user.authorities)
        }
        return null
    }

}
