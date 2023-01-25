package org.example.context;

import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.example.annotation.Autowired;
import org.example.annotation.Component;
import org.example.exception.NoSuchBeanException;
import org.example.exception.NoUniqueBeanException;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ApplicationContextImpl implements ApplicationContext {

    private final Map<String, Object> nameToBeanMap = new HashMap<>();

    public ApplicationContextImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);

        instantiateBeans(reflections.getTypesAnnotatedWith(Component.class));
        injectFields();
    }

    @SneakyThrows
    private void instantiateBeans(Set<Class<?>> beanClasses) {
        for (var beanClass : beanClasses) {
            var constructor = beanClass.getConstructor();
            var instance = constructor.newInstance();
            String beanName = resolveBeanName(beanClass);

            nameToBeanMap.put(beanName, instance);
        }
    }

    private String resolveBeanName(Class<?> beanClass) {
        var value = beanClass.getAnnotation(Component.class).value();
        return value.isBlank() ? StringUtils.uncapitalize(beanClass.getSimpleName()) : value;
    }

    @SneakyThrows
    private void injectFields() {
        for (var bean : nameToBeanMap.values()) {
            for (var field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(bean, getBean(field.getType()));
                }
            }
        }
    }

    public <T> T getBean(Class<T> type) {
        List<Object> beans = nameToBeanMap.values().stream()
                .filter(o -> type.isAssignableFrom(o.getClass()))
                .collect(Collectors.toList());

        if (beans.isEmpty()) {
            throw new NoSuchBeanException();
        } else if (beans.size() > 1) {
            throw new NoUniqueBeanException();
        }

        return type.cast(beans.get(0));
    }

    public <T> T getBean(String name, Class<T> type) {
        var bean = nameToBeanMap.get(name);

        if (bean == null) {
            throw new NoSuchBeanException();
        }

        return type.cast(bean);
    }

    public <T> Map<String, T> getAllBeans(Class<T> type) {
        return nameToBeanMap.entrySet()
                .stream()
                .filter(entry -> type.isAssignableFrom(entry.getValue().getClass()))
                .collect(toMap(Map.Entry::getKey, entry -> type.cast(entry.getValue())));
    }
}
