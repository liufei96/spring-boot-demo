package com.yiyang.demo.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class EsConfig {

    private String host;

    private Integer port;

    private String endpoints;

    private String username;

    private String password;

    /*@Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(host, port, "http")
        ));
    }*/

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        // 账号密码认证
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            final BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
            basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }

        String[] endpointSplit = endpoints.split(",");
        HttpHost[] hosts = new HttpHost[endpointSplit.length];
        for (int i = 0; i < endpointSplit.length; i++) {
            String[] ips = endpointSplit[0].split(":");
            String ip = ips[0];
            int port = Integer.parseInt(ips[1]);
            hosts[i] = new HttpHost(ip, port, HttpHost.DEFAULT_SCHEME_NAME);
        }
        RestClientBuilder builder = RestClient.builder(hosts);
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(30000);
            requestConfigBuilder.setSocketTimeout(30000);
            requestConfigBuilder.setConnectionRequestTimeout(30000);
            return requestConfigBuilder;
        });
        return new RestHighLevelClient(builder);
    }
}
