package com.zjj.netdisk.entity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI 3 documentation using SpringDoc,
 * compatible with Knife4j OpenAPI 3 starter.
 * @author 34978 (adapted for SpringDoc)
 */
@Configuration
// No @EnableSwagger2 or @EnableOpenApi is explicitly needed here,
// as the springdoc-openapi-starter handles the setup.
public class SwaggerConfig { // You can keep SwaggerConfig if you prefer

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                                .title("ems管理系统")
                                .description("ems的api文档")
                                .version("1.0.0")
                                .contact(new Contact()
                                        .name("zjj")
                                        .url("https://github.com/aascer39")
                                        .email("aasc3497836942@gmail.com")
                                )
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("ems-apis")
                .packagesToScan("com.zjj.netdisk.controller")
                .build();
    }
}