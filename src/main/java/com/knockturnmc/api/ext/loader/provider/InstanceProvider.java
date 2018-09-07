package com.knockturnmc.api.ext.loader.provider;

/**
 * Defines an instance provider that is used to instantiate the {@link com.knockturnmc.api.ext.Loadable} class
 */
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