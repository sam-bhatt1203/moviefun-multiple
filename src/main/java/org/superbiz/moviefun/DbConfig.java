package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.springframework.orm.jpa.vendor.Database.MYSQL;

@Configuration
public class DbConfig {
    @Bean
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSource moviesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public HikariDataSource dataSourceAlbum() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(albumsDataSource());
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return hikariDataSource;
    }

    @Bean
    public HikariDataSource dataSourceMovies() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(moviesDataSource());
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return hikariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        jpaVendorAdapter.setGenerateDdl(true);
        return jpaVendorAdapter;
    }

    //Beaxn for Movies
    @Bean
    LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(HibernateJpaVendorAdapter jpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean moviesEntityManager = new LocalContainerEntityManagerFactoryBean();
        moviesEntityManager.setDataSource(dataSourceMovies());
        moviesEntityManager.setJpaVendorAdapter(jpaVendorAdapter);
        moviesEntityManager.setPackagesToScan(DbConfig.class.getPackage().getName());
        moviesEntityManager.setPersistenceUnitName("movies");
        return moviesEntityManager;

    }

    //Bean for Albums
    @Bean
    LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(HibernateJpaVendorAdapter vendorAdapter ){
        LocalContainerEntityManagerFactoryBean albumEntityManager = new LocalContainerEntityManagerFactoryBean();
        albumEntityManager.setDataSource(dataSourceAlbum());
        albumEntityManager.setJpaVendorAdapter(vendorAdapter);
        albumEntityManager.setPackagesToScan(DbConfig.class.getPackage().getName());
        albumEntityManager.setPersistenceUnitName("albums");
        return albumEntityManager;

    }
    @Bean
    PlatformTransactionManager moviesTransactionManager(@Qualifier("moviesEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean factoryBean) {
        return new JpaTransactionManager(factoryBean.getObject());
    }

    @Bean
    PlatformTransactionManager albumsTransactionManager(@Qualifier("albumsEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean factoryBean) {
        return new JpaTransactionManager(factoryBean.getObject());
    }
}
