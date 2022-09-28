package com.tang.config;

import org.springframework.context.annotation.Bean;

public class MyPluginEnableAutoConfiguration {

    public MyPluginEnableAutoConfiguration() {
    }

    @Bean
    MyPluginBeanfactoryConfig myPluginBeanfactoryConfig() {
        return new MyPluginBeanfactoryConfig();
    }
}
