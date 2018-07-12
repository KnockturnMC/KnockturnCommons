package com.knockturnmc.api.ext.loader.provider;

import java.lang.reflect.InvocationTargetException;

public class ReflectiveInstanceProvider implements InstanceProvider {
    /**
     * Returns an instance for the given class
     *
     * @param clazz the class
     * @return the instance
     */
    @Override
    public <T> T getInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Could not build new instance for class " + clazz.getSimpleName(), e);
        }
    }
}
