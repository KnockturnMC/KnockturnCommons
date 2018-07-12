package com.knockturnmc.api.ext.loader.dependency;

import com.knockturnmc.api.ext.loader.ModuleLoaderException;
import com.knockturnmc.api.ext.loader.descriptors.ModuleDescriptor;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleDependenySorter {

    private Map<String, IDModuleDescriptor> descriptorMap;
    private List<IDModuleDescriptor> result = new LinkedList<>();

    /**
     * Creates a new dependency sorter that will sort the given modules descriptors based of their dependencies
     *
     * @param descriptors the descriptors
     */
    public ModuleDependenySorter(Collection<ModuleDescriptor> descriptors) {
        this.descriptorMap = descriptors.stream()
                .map(IDModuleDescriptor::new)
                .collect(Collectors.toMap(IDModuleDescriptor::getID, d -> d));
    }

    /**
     * Sorts the module descriptors
     *
     * @return the module descriptors
     */
    public List<ModuleDescriptor> sort() {
        this.descriptorMap.values().forEach(d -> d.findDependencies(descriptorMap));
        this.descriptorMap.values().forEach(this::sort);

        return result.stream().map(IDModuleDescriptor::getDescriptor).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Sorts a single module descriptor
     *
     * @param descriptor the descriptor
     */
    private void sort(IDModuleDescriptor descriptor) {
        if (descriptor.isSorted()) return;

        descriptor.getDependencies().forEach(this::sort); //Load all dependencies

        result.add(descriptor);
        descriptor.setSorted(true);
    }

    /**
     * A local wrapper class that stores dependency references as well as status during sorting
     */
    private class IDModuleDescriptor {
        private ModuleDescriptor descriptor;

        private String id;
        private Collection<IDModuleDescriptor> dependencies = new ArrayList<>();

        private boolean isSorted;

        /**
         * Creates a new identifiable module descriptor
         *
         * @param descriptor the descriptor
         */
        public IDModuleDescriptor(ModuleDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        /**
         * Returns the id of the descriptor
         *
         * @return the id
         */
        public String getID() {
            return id == null ? (id = toID(descriptor.getName())) : id;
        }

        /**
         * Returns the dependencies
         *
         * @return the collection
         */
        public Collection<IDModuleDescriptor> getDependencies() {
            return dependencies;
        }

        /**
         * Returns if this descriptor has been loaded
         *
         * @return the result
         */
        public boolean isSorted() {
            return isSorted;
        }

        /**
         * Sets if this descriptor has been sorted
         *
         * @param sorted the value
         * @return the descriptor
         */
        public IDModuleDescriptor setSorted(boolean sorted) {
            isSorted = sorted;
            return this;
        }

        /**
         * Looks up the dependencies
         *
         * @param allModules the module map
         * @throws ModuleLoaderException if a module could not be found
         */
        public void findDependencies(Map<String, IDModuleDescriptor> allModules) throws ModuleLoaderException {
            descriptor.getDependencies().forEach(name -> {
                String moduleID = toID(name);

                IDModuleDescriptor dependency = allModules.get(moduleID);
                if (dependency == null) throw new ModuleLoaderException("Could not find denendency " + moduleID + " for module " + descriptor.getName());

                this.dependencies.add(dependency);
            });
        }

        /**
         * Returns the original module descriptor
         *
         * @return the module descriptor
         */
        public ModuleDescriptor getDescriptor() {
            return descriptor;
        }

        /**
         * Transforms a string into an id string
         *
         * @param string the string
         * @return the id string
         */
        private String toID(String string) {
            return string.trim().toLowerCase().replaceAll(" ", "_");
        }
    }
}
