package br.com.mateus.projetoRestPuc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
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