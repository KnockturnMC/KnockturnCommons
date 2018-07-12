package com.knockturnmc.api.ext.loader.descriptors;

import com.knockturnmc.api.ext.Loadable;
import com.knockturnmc.api.ext.Module;
import com.knockturnmc.api.ext.loader.ModuleLoaderException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class ModuleAnnotationDescriptor implements ModuleDescriptor {

    private Module moduleAnnotation;
    private String moduleClassName;

    private File sourceFile;

    public ModuleAnnotationDescriptor(File sourceFile) {
        if (sourceFile == null) throw new NullPointerException("Source file for module was null!");
        this.sourceFile = sourceFile;
    }

    /**
     * Loads the module descriptor from the zip file
     */
    @Override
    public void load() {
        try (ZipFile zip = new ZipFile(this.sourceFile)) {

            try (URLClassLoader preLoader = new URLClassLoader(new URL[]{this.sourceFile.toPath().toUri().toURL()})) {

                ArrayList<? extends Class<?>> moduleClasses = zip.stream()
                        .filter(e -> !e.isDirectory())
                        .filter(e -> e.getName().endsWith(".class"))
                        .map(e -> e.getName().substring(0, e.getName().length() - 6).replace('/', '.'))
                        .map(className -> {
                            try {
                                return Class.forName(className, true, preLoader);
                            } catch (Throwable t) {
                                return null;
                            }
                        }).filter(Objects::nonNull)
                        .filter(c -> c.isAnnotationPresent(Module.class))
                        .filter(Loadable.class::isAssignableFrom)
                        .collect(Collectors.toCollection(ArrayList::new));

                if (moduleClasses.size() < 1) {
                    throw new ModuleLoaderException("Could not load module " + this.sourceFile.getName() + " as it does not contain a class annotated with @Module and extends Loadable");
                } else if (moduleClasses.size() > 1) {
                    throw new ModuleLoaderException("Could not load module " + this.sourceFile.getName() + " as it contains multiple classes annotated with @Module and extedning Loadable");
                } else {
                    Class<?> moduleClass = moduleClasses.get(0);

                    this.moduleAnnotation = moduleClass.getAnnotation(Module.class);
                    this.moduleClassName = moduleClass.getName();
                }
            }

        } catch (IOException e) {
            throw new ModuleLoaderException("Could not load the module " + this.sourceFile.getName() + " as it isn't a zip file");
        }
    }

    /**
     * Returns the module's name
     *
     * @return the name
     */
    @Override
    public String getName() {
        return getModule().map(Module::name).orElseThrow(() -> new ModuleLoaderException("The module was no initialized yet"));
    }

    /**
     * Returns the author
     *
     * @return the author
     */
    @Override
    public String getAuthor() {
        return getModule().map(Module::author).orElseThrow(() -> new ModuleLoaderException("The module was no initialized yet"));
    }

    /**
     * Returns the module version
     *
     * @return the version
     */
    @Override
    public String getVersion() {
        return getModule().map(Module::version).orElseThrow(() -> new ModuleLoaderException("The module was no initialized yet"));
    }

    /**
     * Returns the dependencies this module has
     *
     * @return the dependencies
     */
    @Override
    public Collection<String> getDependencies() {
        return Arrays.asList(getModule().map(Module::dependencies).orElseThrow(() -> new ModuleLoaderException("The module was no initialized yet")));
    }

    /**
     * Returns the module class that this module contains
     *
     * @return the class
     */
    @Override
    public Optional<String> getModuleClassName() {
        return Optional.ofNullable(moduleClassName);
    }

    /**
     * Returns the file this module descriptor is pointing at
     *
     * @return the file
     */
    @Override
    public File getFile() {
        return this.sourceFile;
    }

    /**
     * Returns if the module is designed to be reloadble
     *
     * @return the module
     */
    @Override
    public boolean isReloadable() {
        return getModule().map(Module::reloadable).orElseThrow(() -> new ModuleLoaderException("The module was no initialized yet"));
    }

    /**
     * Returns the module annotation
     *
     * @return the annotation
     */
    private Optional<Module> getModule() {
        return Optional.ofNullable(this.moduleAnnotation);
    }
}
