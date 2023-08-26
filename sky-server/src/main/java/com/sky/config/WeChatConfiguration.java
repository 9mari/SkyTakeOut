package com.sky.config;

import com.sky.properties.WeChatProperties;
import com.sky.utils.WeChatLoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class WeChatConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WeChatLoginUtil weChatLoginUtil(WeChatProperties weChatProperties){
        return new WeChatLoginUtil(weChatProperties.getAppid(),
                weChatProperties.getSecret(),
                weChatProperties.getGrantType(),
                weChatProperties.getUrl());
    }
}
