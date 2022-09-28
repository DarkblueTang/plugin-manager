package com.tang.config;

import com.tang.MyClassLoader;
import com.tang.PluginConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.support.MyPathMatchingResourcePatternResolver;

public class MyPluginRunningConfig implements SpringApplicationRunListener {

    public MyPluginRunningConfig(SpringApplication springApplication, String[] string) {
        PluginConfigProperties.initConfig();
        PluginConfig.initConfig();

        MyPathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new MyPathMatchingResourcePatternResolver();
        springApplication.setResourceLoader(pathMatchingResourcePatternResolver);
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        ((AbstractApplicationContext) context).setClassLoader(MyClassLoader.getMyClassLoader(MyPluginEnableAutoConfiguration.class.getClassLoader()));
    }
}
