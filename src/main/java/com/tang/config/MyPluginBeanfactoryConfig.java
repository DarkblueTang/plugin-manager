package com.tang.config;

import com.tang.MyClassLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MyPluginBeanfactoryConfig implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.setBeanClassLoader(MyClassLoader.getMyClassLoader(MyPluginBeanfactoryConfig.class.getClassLoader()));
    }
}
