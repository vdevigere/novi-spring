package org.novi.web;

import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.FoundActivations;
import org.novi.persistence.ActivationConfigRepository;
import org.novi.web.activations.ComboBooleanActivations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;

@Configuration
public class NoviConfiguration {

    Logger logger = LoggerFactory.getLogger(NoviConfiguration.class);

    public NoviConfiguration(@Value("${activations.plugin.dir}") String plugin_dir, @Autowired ApplicationContext applicationContext) {
        registerActivations(plugin_dir, applicationContext);
    }


    public void registerActivations(String plugin_dir, ApplicationContext applicationContext) {
        logger.debug("Loading plugins from: {}", plugin_dir);
        Map<String, BaseActivation> registry = FoundActivations.REGISTRY.getMap();
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
            if (applicationContext != null) applicationContext.getAutowireCapableBeanFactory().autowireBean(activation);
            registry.put(activation.getClass().getCanonicalName(), activation);
        }
        if (registry.isEmpty()) {
            logger.debug("No Activations found...");
        }
    }

    @Bean
    public ComboBooleanActivations comboBooleanActivations(@Autowired ActivationConfigRepository activationConfigRepository, @Value("${activations.plugin.dir}") String plugin_dir) {
        return new ComboBooleanActivations(activationConfigRepository, scriptEngine(plugin_dir));
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ScriptEngine scriptEngine(String plugin_dir) {
        File pluginDir = new File(plugin_dir);
        URL[] urls = {};
        File[] fList = pluginDir.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
        if (fList != null) {
            urls = Arrays.stream(fList).map(file -> {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(URL[]::new);
        }else{
            try {
                urls = new URL[]{pluginDir.toURI().toURL()};
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        ClassLoader classLoader = URLClassLoader.newInstance(urls, NoviConfiguration.class.getClassLoader());
        ScriptEngine engine = new ScriptEngineManager(classLoader).getEngineByName("scala");
        return engine;
    }
}
