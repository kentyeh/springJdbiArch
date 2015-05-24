package com.github.kentyeh.context;

import com.github.kentyeh.model.TestDao;
import javax.sql.DataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.spring.DBIFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author kent Yeh
 */
@Configuration
@ImportResource({"classpath:testContext.xml"})
@ComponentScan("com.github.kentyeh.manager")
public class TestContext {
    
    @Autowired
    private DataSource datasource;
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JdbiLog jdbiLog() {
        return new JdbiLog();
    }
    
    @Bean
    public DBIFactoryBean dBIFactoryBean() {
        DBIFactoryBean res = new DBIFactoryBean();
        res.setDataSource(datasource);
        return res;
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DBI dbi() throws Exception {
        DBI dbi = (DBI) dBIFactoryBean().getObject();
        dbi.setSQLLog(jdbiLog());
        return dbi;
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TestDao testDao() throws Exception {
        return dbi().open(TestDao.class);
    }
}
