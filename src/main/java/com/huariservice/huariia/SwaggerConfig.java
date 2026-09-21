package com.huariservice.huariia;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("Huari.ia REST API")
                            .description("Documentação da API Huari.ia")
                            .version("1.0.0")
                            .license(new License()
                                    .name("Apache License Version 2.0")
                                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
        }
}
