package com.github.kentyeh.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.kentyeh.model.Dao;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@ImportResource("classpath:applicationContext.xml")
public class ApplicationContext {

    private Jdbi jdbi;
    private ValidatorFactory validatorFactory;

    @Autowired
    public void setJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Autowired
    public void setValidator(final ValidatorFactory validator) {
        this.validatorFactory = validator;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Validator validator() {
        return validatorFactory.getValidator();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ObjectMapper objectMapper() {
        return JsonMapper
                .builder().addModule(new JavaTimeModule())
                .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha512PasswordEncoder();
    }

    @Bean(destroyMethod = "close")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Dao dao() {
        return jdbi.open().attach(Dao.class);
    }
}
