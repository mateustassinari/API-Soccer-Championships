package br.com.mateus.projetoRestPuc.config

import io.swagger.models.auth.In
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.UiConfiguration
import springfox.documentation.swagger.web.UiConfigurationBuilder
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration::class)
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.mateus.projetoRestPuc.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(listOf(ApiKey("Token Access", HttpHeaders.AUTHORIZATION, In.HEADER.name)))
                .securityContexts(listOf(securityContext()))
    }

    private fun securityContext(): SecurityContext? {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.ant("/**"))
            .build()
    }

    fun defaultAuth(): List<SecurityReference?>? {
        val authorizationScope = AuthorizationScope("ADMIN", "accessEverything")
        val authorizationScopes = arrayOfNulls<AuthorizationScope>(1)
        authorizationScopes[0] = authorizationScope
        return listOf(
            SecurityReference("Token Access", authorizationScopes))
    }

    private fun apiInfo(): ApiInfo? {
        return ApiInfo(
                "API PUC Minas - Brazilian Championship",
                "API to handle teams, players and league transfers",
                "Version 1.0",
                "",
                Contact("Mateus", "", "mateustassinari18@gmail.com"),
                "",
                "",
                Collections.emptyList()
        )
    }

    @Bean
    fun uiConfig(): UiConfiguration? {
        return UiConfigurationBuilder.builder()
                .defaultModelsExpandDepth(-1)
                .build()
    }


}