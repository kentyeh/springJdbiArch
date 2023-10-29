package com.github.kentyeh.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.kentyeh.manager.MemberManager;
import com.github.kentyeh.model.Member;
import com.github.kentyeh.model.TestDao;
import java.security.Principal;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author kent Yeh
 */
@Configuration
@ImportResource({"classpath:testContext.xml", "classpath:applicationContext-security.xml"})
@ComponentScan("com.github.kentyeh.manager")
@EnableCaching
public class TestContext {

    private Jdbi jdbi;

    private MemberManager memberManager;

    @Autowired
    public void setJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Autowired
    public void setMemberManager(MemberManager memberManager) {
        this.memberManager = memberManager;
    }

    @Bean(destroyMethod = "close")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TestDao testDao() {
        return jdbi.open().attach(TestDao.class);
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

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Principal testPrincipal(String account) {
        Member member = memberManager.findByPrimaryKey(account);
        UserDetails userDetails = new CustomUserInfo(member);
        return new UsernamePasswordAuthenticationToken(userDetails, member.getPassword());
    }

    @Bean
    public CustomUserService customUserService() {
        return new CustomUserService();
    }
}
