package com.knockturnmc.api.ext.loader.descriptors;

import com.knockturnmc.api.ext.loader.ModuleLoaderException;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * Represents a module descriptor that holds information about the module file found under {@link ModuleDescriptor#getFile()}
 */
public interface ModuleDescriptor {

    /**
     * Loads the module descriptor from the zip file
     */
    void load();

    /**
     * Loads the module descriptor silently
     *
     * @return if the descriptor was loaded successfuly
     */
    default boolean loadSilently(Logger logger) {
        try {
            this.load();
            return true;
        } catch (ModuleLoaderException e) {
            logger.error("Could not load module ", e);
            return false;
        }
    }

    /**
     * Returns the module's name
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the author
     *
     * @return the author
     */
    String getAuthor();

    /**
     * Returns the module version
     *
     * @return the version
     */
    String getVersion();

    /**
     * Returns the dependencies this module has
     *
     * @return the dependencies
     */
    Collection<String> getDependencies();

    /**
     * Returns the module class that this module contains
     *
     * @return the class
     */
    Optional<String> getModuleClassName();

    /**
     * Returns the file this module descriptor is pointing at
     *
     * @return the file
     */
    File getFile();

    /**
     * Returns if the module is designed to be reloadble
     *
     * @return the module
     */
    boolean isReloadable();

}
