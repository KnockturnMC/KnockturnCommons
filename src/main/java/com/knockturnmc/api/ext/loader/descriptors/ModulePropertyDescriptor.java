package com.knockturnmc.api.ext.loader.descriptors;

import com.knockturnmc.api.ext.loader.ModuleLoaderException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModulePropertyDescriptor implements ModuleDescriptor {

    private Properties properties;

    private File sourceFile;

    public ModulePropertyDescriptor(File sourceFile) {
        if (sourceFile == null) throw new NullPointerException("Source file for module was null!");

        this.sourceFile = sourceFile;
        this.properties = new Properties();
    }

    /**
     * Loads the module descriptor from the zip file
     */
    @Override
    public void load() {
        try (ZipFile zip = new ZipFile(this.sourceFile)) {

            ZipEntry entry = zip.getEntry("module.properties");
            if (entry == null) throw new ModuleLoaderException("Could not load module as it is missing a module.properties file");

            try (InputStream propertiesStream = zip.getInputStream(entry)) {
                this.properties.load(propertiesStream);
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
        return properties.getProperty("name", sourceFile.getName());
    }

    /**
     * Returns the author
     *
     * @return the author
     */
    @Override
    public String getAuthor() {
        return properties.getProperty("author", "");
    }

    /**
     * Returns the module version
     *
     * @return the version
     */
    @Override
    public String getVersion() {
        return properties.getProperty("version", "");
    }

    /**
     * Returns the dependencies this module has
     *
     * @return the dependencies
     */
    @Override
    public Collection<String> getDependencies() {
        List<String> dependencies = new ArrayList<>(Arrays.asList(properties.getProperty("dependencies", "").split(",")));
        dependencies.removeIf(String::isEmpty);
        return dependencies;
    }

    /**
     * Returns the module class that this module contains
     *
     * @return the class
     */
    @Override
    public Optional<String> getModuleClassName() {
        return Optional.ofNullable(properties.getProperty("main"));
    }

    /**
     * Returns the file this module descriptor is pointing at
     *
     * @return the file
     */
    @Override
    public File getFile() {
        return sourceFile;
    }

    /**
     * Returns if the module is designed to be reloadble
     *
     * @return the module
     */
    @Override
    public boolean isReloadable() {
        return Boolean.parseBoolean(properties.getProperty("reloadable", "true"));
    }
}
