package com.tang;

import com.tang.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "hhd.plugin")
@Setter
@Getter
public class PluginConfigProperties {
    @Getter
    static PluginConfigProperties properties;

    String basePath = ".";
    List<PluginProperties> plugins = new LinkedList<>();
    List<String> modules = new LinkedList<>();
    static String resourceFileName = "plugin-config.yml";

    public static void initConfig() {
        Yaml yaml = new Yaml();
        InputStream inputStream = PluginConfigProperties.class
                .getClassLoader()
                .getResourceAsStream(resourceFileName);
        properties = yaml.loadAs(inputStream, PluginConfigProperties.class);
        if (CommonUtils.isEmpty(properties)) {
            properties = new PluginConfigProperties();
        }
    }
}

