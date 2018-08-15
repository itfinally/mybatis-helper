package top.itfinally.mybatis.generator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Configuration
@ConfigurationProperties( prefix = "spring.datasource" )
public class DataSourceProperties {

    private String url;

    private String username;

    private String password;

    private String databaseId;

    public String getUrl() {
        return url;
    }

    public DataSourceProperties setUrl( String url ) {
        this.url = url;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DataSourceProperties setUsername( String username ) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DataSourceProperties setPassword( String password ) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "DataSourceConfig{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
