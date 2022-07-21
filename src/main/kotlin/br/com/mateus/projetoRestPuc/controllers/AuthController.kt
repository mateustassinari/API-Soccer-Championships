package br.com.mateus.projetoRestPuc.controllers

import br.com.mateus.projetoRestPuc.dtos.TokenDto
import br.com.mateus.projetoRestPuc.dtos.LoginDto
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController() {

    @ApiOperation("Login")
    @PostMapping("/login")
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "Login successfully"),
            ApiResponse(code = 403, message = "Incorrect Data/Authentication Failure"),
            ApiResponse(code = 400, message = "Lack of information"),
            ApiResponse(code = 500, message = "Unexpected error")]
    )
    fun fakeLogin(@Valid @RequestBody objDto: LoginDto): ResponseEntity<TokenDto> {
        throw IllegalStateException("This method shouldn't be called. It's implemented by Spring Security filters.")
    }

}