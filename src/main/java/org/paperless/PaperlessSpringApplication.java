package org.paperless;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@ComponentScan(
        basePackages = {"org.paperless", "org.paperless.api", "org.paperless.configuration", "org.paperless.persistence.entities", "org.paperless.persistence.repositories"},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableJpaRepositories(
        basePackages = "org.paperless.persistence.repositories"
)
public class PaperlessSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaperlessSpringApplication.class, args);
    }

    @Bean(name = "org.paperless.OpenApiGeneratorApplication.jsonNullableModule")
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}