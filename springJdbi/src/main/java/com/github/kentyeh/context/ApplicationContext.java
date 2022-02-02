package com.github.kentyeh.context;

import com.github.kentyeh.model.Dao;
import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSocketConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@ImportResource("classpath:applicationContext.xml")
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 30 * 60)
public class ApplicationContext {

    private static final Logger logger = LogManager.getLogger(ApplicationContext.class);
    private Jdbi jdbi;

    @Autowired
    public void setJdbi(Jdbi jdbi) {
        this.jdbi = jdbi;
        this.jdbi.setSqlLogger(jdbiLog());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JdbiLog jdbiLog() {
        return new JdbiLog();
    }

    @Bean(destroyMethod = "close")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Dao dao() {
        return jdbi.open().attach(Dao.class);
    }

    @Bean
    public CacheManager springCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        return RedisCacheManager.create(lettuceConnectionFactory);
    }

    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        return new GenericObjectPoolConfig();
    }

    @Bean("prodLettuceConnFactory")
    @Conditional(LettuceConnFactoryCondition.class)
    public LettuceConnectionFactory connectionFactory(GenericObjectPoolConfig gopc) {
        logger.info("product 4 redis");
        RedisSocketConfiguration config = new RedisSocketConfiguration("/var/run/redis/redis.sock");
        gopc.setMaxTotal(10);
        gopc.setMaxIdle(3);
        gopc.setMinIdle(1);
        gopc.setMaxWait(Duration.ofSeconds(10));
        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(gopc).build();
        return new LettuceConnectionFactory(config, poolConfig);
    }

    @Bean("devConfigRedisAction")
    @Conditional(LettuceConnFactoryCondition.class)
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean("devLettuceConnFactory")
    @Conditional(LettuceConnFactoryCondition.class)
    public LettuceConnectionFactory redisDevConnectionFactory(GenericObjectPoolConfig gopc) {
        logger.info("develop 4 redis");
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);

        LettucePoolingClientConfiguration poolConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(gopc).build();
        return new LettuceConnectionFactory(config, poolConfig);
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
