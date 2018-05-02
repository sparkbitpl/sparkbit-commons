package pl.sparkbit.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
public class YamlDefaultEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DEFAULTS_FILE_NAME = "defaults.yml";

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        try {
            Enumeration<URL> resources = this
                    .getClass()
                    .getClassLoader()
                    .getResources(DEFAULTS_FILE_NAME);

            ArrayList<URL> aList = Collections.list(resources);
            aList.forEach((path) -> tryLoadingResource(path, environment));

        } catch (IOException ex) {
            log.error("Failed while searching for defaults configuration");
        }
    }

    private void tryLoadingResource(URL path, ConfigurableEnvironment environment) {
        Resource resource = new UrlResource(path);
        try {
            List<PropertySource<?>> propertySources = this.loader.load(path.toString(), resource);
            propertySources.forEach((propertySource) -> environment.getPropertySources().addLast(propertySource));
        } catch (IOException e) {
            log.error("Failed while loading default configuration property file {}", path);
        }
    }

}
