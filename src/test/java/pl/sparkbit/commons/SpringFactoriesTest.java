package pl.sparkbit.commons;

import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringFactoriesTest {

    @Test
    public void testRegisteredAutoConfigurations() {
        List<String> classNames = loadFactoryNames(EnableAutoConfiguration.class);
        List<? extends Class<?>> autoConfigurationClasses = classNames.stream().map(this::loadClass).collect(Collectors.toList());
        assertThat(autoConfigurationClasses).allMatch(new HasAnnotation(Configuration.class)).hasSize(10);
    }

    private Class<?> loadClass(String name) {
        try {
            return ClassUtils.forName(name, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> loadFactoryNames(Class<?> aClass) {
        try (InputStream is = SpringFactoriesTest.class.getClassLoader().getResourceAsStream("META-INF/spring.factories")) {
            if (is == null) {
                throw new RuntimeException("Cannot load spring.factories");
            }
            Properties props = new Properties();
            props.load(is);
            String value = props.getProperty(aClass.getName());
            if (value == null) {
                return Collections.emptyList();
            }
            return Stream.of(value.split(",")).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Cannot load " + aClass, e);
        }
    }

    @RequiredArgsConstructor
    private static final class HasAnnotation implements Predicate<Class<?>> {

        private final Class<? extends Annotation> expectedAnnotation;

        @Override
        public boolean test(Class<?> aClass) {
            return aClass.isAnnotationPresent(expectedAnnotation);
        }
    }
}
