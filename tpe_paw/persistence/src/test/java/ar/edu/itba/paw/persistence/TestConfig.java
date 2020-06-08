package ar.edu.itba.paw.persistence;

import org.hsqldb.jdbc.JDBCDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@ComponentScan({ "ar.edu.itba.paw.persistence"})
@Configuration
public class TestConfig {

    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriverClass(JDBCDriver.class);
        ds.setUrl("jdbc:hsqldb:mem:paw");
        ds.setUsername("ha");
        ds.setPassword("");
        return ds;
    }

//    @Bean
//    public DataSourceInitializer dataSourceInitializer(){
//        DataSourceInitializer dsi = new DataSourceInitializer();
//        dsi.setDataSource(dataSource());
//        dsi.setDatabasePopulator(databasePopulator());
//        return dsi;
//    }

//    @Bean
//    public DataSource dataSource() {
//        final SimpleDriverDataSource ds = new SimpleDriverDataSource();
//        ds.setDriverClass(org.postgresql.Driver.class);
////        ds.setUrl("jdbc:postgresql://localhost/paw-2020a-2");
////        ds.setUsername("paw-2020a-2");
////        ds.setPassword("em8TT4uvx");
//        ds.setUrl("jdbc:postgresql://localhost/paw");
//        ds.setUsername("postgres");
//        ds.setPassword("postgres");
//        return ds;
//    }

//    @Bean
//    public DataSourceInitializer dataSourceInitializer(final DataSource ds){
//        DataSourceInitializer dsi = new DataSourceInitializer();
//        dsi.setDataSource(ds);
//        dsi.setDatabasePopulator(databasePopulator());
//        return dsi;
//    }
//
//    public DatabasePopulator databasePopulator(){
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.addScript(schemaSql);
//        return populator;
//    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(dataSource());

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update"); // Research and consider using "validate"!
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");

        factoryBean.setJpaProperties(properties);

        return factoryBean;
    }
}
