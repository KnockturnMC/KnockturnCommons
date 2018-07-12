package com.knockturnmc.api.ext.loader;

import com.knockturnmc.api.ext.Loadable;
import com.knockturnmc.api.ext.loader.descriptors.ModuleDescriptor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleWrapper<T> {

    private ModuleDescriptor descriptor;
    private Loadable<T> module;

}
