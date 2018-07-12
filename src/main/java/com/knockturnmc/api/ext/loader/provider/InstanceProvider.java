package com.knockturnmc.api.ext.loader.provider;

public interface InstanceProvider {

    /**
     * Returns an instance for the given class
     *
     * @param clazz the class
     * @param <T>   the class type
     * @return the instance
     */
    <T> T getInstance(Class<T> clazz);

}