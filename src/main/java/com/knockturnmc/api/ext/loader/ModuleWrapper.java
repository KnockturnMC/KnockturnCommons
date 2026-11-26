package com.knockturnmc.api.ext.loader;

import com.knockturnmc.api.ext.Loadable;
import com.knockturnmc.api.ext.loader.descriptors.ModuleDescriptor;

public class ModuleWrapper<T> {

    private ModuleDescriptor descriptor;
    private Loadable<T> module;

    public ModuleWrapper(final ModuleDescriptor descriptor, final Loadable<T> module) {
        this.descriptor = descriptor;
        this.module = module;
    }

    public ModuleDescriptor getDescriptor() {
        return descriptor;
    }

    public Loadable<T> getModule() {
        return module;
    }
}
