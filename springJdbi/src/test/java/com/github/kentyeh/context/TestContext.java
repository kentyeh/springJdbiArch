package com.github.kentyeh.context;

import com.github.kentyeh.model.TestDao;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSocketConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *
 * @author kent Yeh
 */
@Configuration
@ImportResource({"classpath:testContext.xml", "classpath:applicationContext-security.xml"})
@ComponentScan("com.github.kentyeh.manager")
@EnableCaching
@EnableRedisHttpSession()
public class TestContext {

    private static final Logger logger = LogManager.getLogger(TestContext.class);
    private Jdbi jdbi;

    @Autowired
    public void setJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Bean(destroyMethod = "close")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TestDao testDao() {
        return jdbi.open().attach(TestDao.class);
    }

    @Bean
    public CustomUserService customUserService() {
        return new CustomUserService();
    }

    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("prodLettuceConnFactory")
    @Conditional(LettuceConnFactoryCondition.class)
    public LettuceConnectionFactory connectionFactory(GenericObjectPoolConfig gopc) {
        logger.error("product 4 redis");
        RedisSocketConfiguration config = new RedisSocketConfiguration("/var/run/redis/redis.sock");
        gopc.setMaxTotal(10);
        gopc.setMaxIdle(3);
        gopc.setMinIdle(1);
        gopc.setMaxWait(Duration.ofSeconds(10));
        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(gopc).build();
        return new LettuceConnectionFactory(config, poolConfig);
    }

    @Bean("devLettuceConnFactory")
    @DependsOn("devRedisServer")
    @Conditional(LettuceConnFactoryCondition.class)
    public LettuceConnectionFactory redisDevConnectionFactory(GenericObjectPoolConfig gopc) {
        logger.error("develop 4 redis");
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);

        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(gopc)
                .build();
        return new LettuceConnectionFactory(config, poolConfig);
    }

    @Bean
    public CacheManager springCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        return RedisCacheManager.create(lettuceConnectionFactory);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RedisAtomicLong redisAtomicLong(String redisCounter, RedisConnectionFactory factory, long initialValue) {
        return new RedisAtomicLong(redisCounter, factory, initialValue);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RedisAtomicLong redisAtomicLong(String redisCounter, RedisConnectionFactory factory) {
        return new RedisAtomicLong(redisCounter, factory);
    }
}
