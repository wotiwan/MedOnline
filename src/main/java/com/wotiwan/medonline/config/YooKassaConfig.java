package com.wotiwan.medonline.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "yookassa")
public class YooKassaConfig {
    private String shopId;
    private String secretKey;
}