package com.kabigon.kabigonapiclientsdk;

import com.kabigon.kabigonapiclientsdk.client.KabigonApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author kabigon
 * @version 2023/1/24/00:48
 */
@Data
@Configuration
@ComponentScan
@ConfigurationProperties("kabigon.api")
public class KabigonApiConfig {

    private String accessKey;

    private String secretKey;

    @Bean
    public KabigonApiClient kabigonApiClient() {
        return new KabigonApiClient(accessKey, secretKey);
    }

}
