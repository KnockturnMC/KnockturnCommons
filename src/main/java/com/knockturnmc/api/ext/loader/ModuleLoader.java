package com.knockturnmc.api.ext.loader;

import com.knockturnmc.api.ext.Loadable;
import com.knockturnmc.api.ext.loader.dependency.ModuleDependenySorter;
import com.knockturnmc.api.ext.loader.descriptors.ModuleAnnotationDescriptor;
import com.knockturnmc.api.ext.loader.descriptors.ModuleDescriptor;
import com.knockturnmc.api.ext.loader.provider.InstanceProvider;
import com.knockturnmc.api.ext.loader.provider.ReflectiveInstanceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A module loader that can load modules from the system into the runtime
 *
 * @param <T> the instance that will be used to enable the modules
 */
public class ModuleLoader<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Collection<ModuleWrapper<T>> loadedModules = new ArrayList<>();
    private final File moduleDirectory;
    private final String fileEnding;

    private final InstanceProvider instanceProvider;
    private final ModuleDescriptorProvider descriptorProvider;

    private Class<? extends Loadable<T>> customModuleType;

    /**
     * Creates a new module loader for jars in the given module directory
     *
     * @param moduleDirectory the module directory
     */
    public ModuleLoader(File moduleDirectory) {
        this(moduleDirectory, ".jar");
    }

    /**
     * Creates a new module loader for the given module folder and ending
     *
     * @param moduleDirectory the module directory
     * @param fileEnding      the file ending
     */
    public ModuleLoader(File moduleDirectory, String fileEnding) {
        this(moduleDirectory, fileEnding, new ReflectiveInstanceProvider());
    }

    /**
     * Creates a new module loader for the given module folder and ending
     *
     * @param moduleDirectory  the module directory
     * @param fileEnding       the file ending
     * @param instanceProvider the instance provider this module loader will use
     */
    public ModuleLoader(File moduleDirectory, String fileEnding, InstanceProvider instanceProvider) {
        this(moduleDirectory, fileEnding, instanceProvider, ModuleAnnotationDescriptor::new);
    }

    /**
     * Creates a new module loader for the given folder, the file ending, the instance provider and descriptor supplier
     *
     * @param moduleDirectory    the module directory
     * @param fileEnding         the ending of the module files
     * @param instanceProvider   the instance provider for the module classes
     * @param descriptorProvider the provider of the new descriptor instances
     */
    public ModuleLoader(File moduleDirectory, String fileEnding, InstanceProvider instanceProvider, ModuleDescriptorProvider descriptorProvider) {
        this.moduleDirectory = moduleDirectory;
        this.fileEnding = fileEnding;
        this.instanceProvider = instanceProvider;
        this.descriptorProvider = descriptorProvider;
    }

    /**
     * Loads the module into the runtime, but will not enable them
     */
    public void loadModules() {
        if (!moduleDirectory.isDirectory()) {
            if (moduleDirectory.exists()) {
                if (!moduleDirectory.delete()) throw new ModuleLoaderException("Could not delete file " + moduleDirectory + " to create module directory");
            }

            if (!moduleDirectory.mkdirs()) throw new ModuleLoaderException("Could not create module directory");
        }

        File[] files = moduleDirectory.listFiles(f -> !f.isDirectory() && f.getName().endsWith(fileEnding));
        if (files == null) return;

        Set<ModuleDescriptor> descriptors = Arrays.stream(files) //Load descriptors
                .map(descriptorProvider::newInstance)
                .filter(d -> d.loadSilently(logger))
                .collect(Collectors.toSet());

        List<ModuleDescriptor> sortedDescriptors = new ModuleDependenySorter(descriptors).sort();
        sortedDescriptors.forEach(descriptor -> {
            try {
                loadModule(descriptor);
            } catch (ModuleLoaderException e) {
                logger.error("Could not load module " + descriptor.getName(), e);
            }
        });

        logger.info("Loaded " + getLoadedModules().size() + " modules");
    }

    /**
     * Loads the module
     *
     * @param descriptor the descriptor
     */
    public void loadModule(ModuleDescriptor descriptor) {
        String mainClassPath = descriptor.getModuleClassName()
                .orElseThrow(() -> new ModuleLoaderException("Could not load the descriptor as it did not locate a main class"));

        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{descriptor.getFile().toURI().toURL()}, getClass().getClassLoader());

            Class<?> mainClass = Class.forName(mainClassPath, true, urlClassLoader);
            Class<?> moduleType = this.customModuleType != null ? this.customModuleType : Loadable.class;

            if (!moduleType.isAssignableFrom(mainClass)) {
                throw new ModuleLoaderException("Could not load module as main class" + mainClassPath + " is not assingable from " + moduleType.getSimpleName());
            }

            try {
                Class<? extends Loadable<T>> genericModuleMainClass = (Class<? extends Loadable<T>>) mainClass.asSubclass(moduleType);
                Loadable<T> moduleInstance = this.instanceProvider.getInstance(genericModuleMainClass);

                this.loadedModules.add(new ModuleWrapper<>(descriptor, moduleInstance));
                this.logger.info("Loaded module " + descriptor.getName() + " by " + descriptor.getAuthor());
            } catch (ClassCastException e) {
                throw new ModuleLoaderException("Could not load descriptor as main class is not assignable", e);
            }
        } catch (MalformedURLException e) {
            throw new ModuleLoaderException("Could not load descriptor " + descriptor.getName() + " as it cannot be converted to an URL", e);
        } catch (ClassNotFoundException e) {
            throw new ModuleLoaderException("Could not find main class" + mainClassPath + " in module " + descriptor.getName(), e);
        }
    }

    /**
     * Unloads the modules loaded by this loader
     */
    public void unloadModules() {
        this.loadedModules.forEach(wrapper -> {
            try {
                unloadModule(wrapper);
            } catch (ModuleLoaderException e) {
                logger.error("Error unloading module", e);
            }
        });
    }

    /**
     * Unloads the given module
     *
     * @param wrapper the module wrapper
     */
    public void unloadModule(ModuleWrapper<T> wrapper) {
        ClassLoader classLoader = wrapper.getClass().getClassLoader();
        if (!(classLoader instanceof URLClassLoader)) throw new ModuleLoaderException("Could not unload module " + wrapper.getDescriptor().getName() + " as it does not use an URLClassLoader");

        try {
            ((URLClassLoader) classLoader).close();
        } catch (IOException e) {
            throw new ModuleLoaderException("Could not close URLClassLoader of " + wrapper.getDescriptor().getName(), e);
        }
    }

    /**
     * Enables all modules
     *
     * @param parent the parent to enable the modules with
     */
    public void enableModules(T parent) {
        this.loadedModules.forEach(w -> this.enableModule(parent, w));
    }

    /**
     * Enables the module
     *
     * @param parent  the parent used for enabeling
     * @param wrapper the module warpper
     */
    public void enableModule(T parent, ModuleWrapper<T> wrapper) {
        wrapper.getModule().onEnable(parent);
    }

    /**
     * Disables all modules
     *
     * @param parent the parent to disable the modules with
     */
    public void disableModules(T parent) {
        this.loadedModules.forEach(w -> this.disableModule(parent, w));
    }

    /**
     * Disables the module
     *
     * @param parent  the parent used for disabling
     * @param wrapper the module warpper
     */
    public void disableModule(T parent, ModuleWrapper<T> wrapper) {
        wrapper.getModule().onDisable(parent);
    }

    /**
     * Returns the list of loaded module
     *
     * @return the list
     */
    public Collection<Loadable<T>> getLoadedModules() {
        return loadedModules.stream().map(ModuleWrapper::getModule).collect(Collectors.toCollection(ArrayList::new));
    }
}
