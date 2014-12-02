package com.prylutskyi.blackjack.config;

import com.prylutskyi.blackjack.dao.AccountDao;
import com.prylutskyi.blackjack.dao.ActionDao;
import com.prylutskyi.blackjack.dao.GameDao;
import com.prylutskyi.blackjack.dao.TransactionDao;
import com.prylutskyi.blackjack.dao.impl.AccountDaoImpl;
import com.prylutskyi.blackjack.dao.impl.ActionDaoImpl;
import com.prylutskyi.blackjack.dao.impl.GameDaoImpl;
import com.prylutskyi.blackjack.dao.impl.TransactionDaoImpl;
import com.prylutskyi.blackjack.hsqldb.HSQLDBServer;
import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Patap on 27.11.2014.
 */

@Configuration
@EnableTransactionManagement
public class PersistenceConfig {

    public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    public static final String HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";
    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";

    public static final String DATABASE_FILE = "database.0";
    public static final String DATABASE_NAME = "dbname.0";
    public static final String SERVER_REMOTE_OPEN = "remote_open";
    public static final String RECONFIG_LOGGING = "reconfig_logging";

    @Autowired
    private Environment env;

    @Bean
    public AccountDao accountDao() {
        return new AccountDaoImpl(sessionFactory());
    }

    @Bean
    public GameDao gameDao() {
        return new GameDaoImpl(sessionFactory());
    }

    @Bean
    public TransactionDao transactionDao() {
        return new TransactionDaoImpl(sessionFactory());
    }

    @Bean
    public ActionDao actionDao() {
        return new ActionDaoImpl(sessionFactory());
    }

    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource());
        sessionBuilder.addProperties(getHibernateProperties());
        sessionBuilder.addResource("com/prylutskyi/blackjack/vo/Account.hbm.xml");
        sessionBuilder.addResource("com/prylutskyi/blackjack/vo/Action.hbm.xml");
        sessionBuilder.addResource("com/prylutskyi/blackjack/vo/Game.hbm.xml");
        sessionBuilder.addResource("com/prylutskyi/blackjack/vo/Transaction.hbm.xml");
        return sessionBuilder.buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager transactionManager(
            SessionFactory sessionFactory) {
        return new HibernateTransactionManager(
                sessionFactory);
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty(JDBC_DRIVER_CLASS_NAME));
        dataSource.setUrl(env.getProperty(JDBC_URL));
        dataSource.setUsername(env.getProperty(JDBC_USERNAME));
        dataSource.setPassword(env.getProperty(JDBC_PASSWORD));
        return dataSource;
    }

    @Bean
    public SmartLifecycle smartLifecycle() {
        return new HSQLDBServer(getHsqldbProperties());
    }

    private Properties getHsqldbProperties() {
        Properties prop = new Properties();
        putProperty(prop, DATABASE_FILE);
        putProperty(prop, DATABASE_NAME);
        putProperty(prop, SERVER_REMOTE_OPEN);
        putProperty(prop, RECONFIG_LOGGING);
        return prop;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        putProperty(properties, HIBERNATE_SHOW_SQL);
        putProperty(properties, HIBERNATE_DIALECT);
        putProperty(properties, HIBERNATE_HBM2DDL_AUTO);
        return properties;
    }

    private void putProperty(Properties prop, String key) {
        prop.put(key, env.getProperty(key));
    }

}
