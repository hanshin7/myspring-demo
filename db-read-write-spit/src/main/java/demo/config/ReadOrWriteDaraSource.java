package demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import demo.entity.SlaveDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * create by hanshin on 2021/4/19
 */
@Configuration
@MapperScan("demo.mapper")
public class ReadOrWriteDaraSource {
    /**
     * 主库配置注入
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource master() {
        //new DruidDataSource();
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 从库配置注入
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slave() {
        //new DruidDataSource();
        return DruidDataSourceBuilder.create().build();
    }
    /**
     * 主从动态配置
     */
    @Bean
    public SlaveDataSource dynamic(@Qualifier("master") DataSource masterDataSource,
                                   @Autowired(required = false) @Qualifier("slave") DataSource slaveDataSource) {
        SlaveDataSource dynamicDataSource = new SlaveDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);
        if (slaveDataSource != null) {
            targetDataSources.put("slave", slaveDataSource);
        }
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource);
        return dynamicDataSource;
    }
    /*
     * 多个数据源需要配置SqlSessionFactory
     * */
    @Bean
    public SqlSessionFactory sessionFactory(@Qualifier("dynamic") DataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setTypeAliasesPackage("demo.mapper");
        bean.setDataSource(dynamicDataSource);
        return bean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlTemplate(@Qualifier("sessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "dataSource")
    public DataSourceTransactionManager dataSource(@Qualifier("dynamic") DataSource dynamicDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dynamicDataSource);
        return dataSourceTransactionManager;
    }
}
