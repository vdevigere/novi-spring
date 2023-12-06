package org.novi.web;

import org.novi.core.activations.BaseActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

@Configuration
public class NoviConfiguration {


    private final String plugin_dir;
    Logger logger = LoggerFactory.getLogger(NoviConfiguration.class);

    public NoviConfiguration(@Value("${activations.plugin.dir}") String plugin_dir) {
        this.plugin_dir = plugin_dir;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Map<String, BaseActivation> foundActivations() {
        logger.debug("Loading plugins from: {}", plugin_dir);
        Map<String, BaseActivation> found = new HashMap<>();
        ServiceLoader<BaseActivation> loader = ServiceLoader.load(BaseActivation.class);
        File pluginDir = new File(plugin_dir);
        File[] fList = pluginDir.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
        if (fList != null) {
            URL[] urls = Arrays.stream(fList).map(file -> {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(URL[]::new);
            loader = ServiceLoader.load(BaseActivation.class, URLClassLoader.newInstance(urls, NoviConfiguration.class.getClassLoader()));
        }
        for (BaseActivation activation : loader) {
            logger.debug("Found Activation: {}", activation.getClass().getCanonicalName());
            found.put(activation.getClass().getCanonicalName(), activation);
        }
        if (found.isEmpty()) {
            logger.debug("No Activations found...");
        }
        return found;
    }
}
