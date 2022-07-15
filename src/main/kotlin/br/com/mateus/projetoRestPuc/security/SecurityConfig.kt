package br.com.mateus.projetoRestPuc.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**",
            "/swagger-ui.html", "/webjars/**"
        )
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable().authorizeRequests()
            .antMatchers(HttpMethod.GET, "/**").permitAll()
            .anyRequest().authenticated()
            .and().exceptionHandling()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));


        http.addFilter(getJWTAuthenticationFilter())
        http.addFilter(
            JWTAuthorizationFilter(
                authenticationManager(),
                jwtUtil = jwtUtil,
                userDetailService = userDetailsService
            )
        )
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun getJWTAuthenticationFilter(): JWTAuthenticationFilter? {
        val filter = JWTAuthenticationFilter(authenticationManager(), jwtUtil = jwtUtil)
        filter.setFilterProcessesUrl("/auth/login")
        return filter
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val configuration = CorsConfiguration().applyPermitDefaultValues()
        configuration.allowedMethods = listOf("POST", "GET", "PUT", "DELETE", "OPTIONS")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(inMemoryDaoAuthenticationProvider());
    }

    @Bean
    fun inMemoryUserDetailsService(): UserDetailsService? {
        val user1: UserDetails = User.builder()
            .username("teste_all")
            .password("123")
            .roles("ADMIN")
            .passwordEncoder { password -> passwordEncoder().encode(password) }
            .build()
        val user2: UserDetails = User.builder()
            .username("teste")
            .password("123")
            .roles("NOTDELETE")
            .passwordEncoder { password -> passwordEncoder().encode(password) }
            .build()
        return InMemoryUserDetailsManager(user1,user2)
    }

    @Bean
    fun inMemoryDaoAuthenticationProvider(): DaoAuthenticationProvider? {
        val daoAuthenticationProvider = DaoAuthenticationProvider()
        daoAuthenticationProvider.setUserDetailsService(inMemoryUserDetailsService())
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder())
        return daoAuthenticationProvider
    }

}