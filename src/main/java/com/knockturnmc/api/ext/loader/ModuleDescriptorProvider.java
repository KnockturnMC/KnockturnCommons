package com.knockturnmc.api.ext.loader;

import com.knockturnmc.api.ext.loader.descriptors.ModuleDescriptor;

import java.io.File;

/**
 * Defines a provider for a {@link ModuleDescriptorProvider} instance that the {@link ModuleLoader} uses to read a module file
 */
public interface ModuleDescriptorProvider {

    /**
     * Returns a new empty instance of a module descriptor
     *
     * @param source the source file
     * @return the descriptor
     */
    ModuleDescriptor newInstance(File source);

}
