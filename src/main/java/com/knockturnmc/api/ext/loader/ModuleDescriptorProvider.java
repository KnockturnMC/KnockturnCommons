package com.knockturnmc.api.ext.loader;

import com.knockturnmc.api.ext.loader.descriptors.ModuleDescriptor;

import java.io.File;

public interface ModuleDescriptorProvider {

    /**
     * Returns a new empty instance of a module descriptor
     *
     * @param source the source file
     * @return the descriptor
     */
    ModuleDescriptor newInstance(File source);

}
