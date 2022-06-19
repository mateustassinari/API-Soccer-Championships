package br.com.mateus.projetoRestPuc.controllers

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.web.bind.annotation.*

@RestController
class HealthController {

    @ApiOperation("Check application status")
    @GetMapping("/actuator/health")
    @ApiResponses(value = [ApiResponse(code = 200, message = "Successful request/Shows the status of the application, which will be UP if the application is healthy and DOWN if it is not healthy due to any problem"),
        ApiResponse(code = 500, message = "Unexpected error")] )
    fun fakeHealth(): Void {
        throw IllegalStateException("This method shouldn't be called.")
    }



}