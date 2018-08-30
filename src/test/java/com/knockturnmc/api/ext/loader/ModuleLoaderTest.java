package com.knockturnmc.api.ext.loader;

import com.knockturnmc.api.ext.loader.descriptors.ModulePropertyDescriptor;
import com.knockturnmc.api.ext.loader.provider.ReflectiveInstanceProvider;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ModuleLoaderTest {

    @Test
    public void testLoader() {
        File moduleFile = new File(System.getProperty("user.dir") + "/src/test/resources");
        AnnotationLoader loader = new AnnotationLoader(moduleFile);

        test(loader);
    }

    @Test
    public void testPropertyLoader() {
        File moduleFile = new File(System.getProperty("user.dir") + "/src/test/resources");
        PropertyLoader loader = new PropertyLoader(moduleFile);

        test(loader);
    }

    /**
     * Tests a specific loader implementation
     *
     * @param loader the implementation
     */
    private void test(ModuleLoader<AtomicBoolean> loader) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        loader.loadModules();

        loader.enableModules(atomicBoolean);
        assertEquals(2, loader.getLoadedModules().size());
        assertTrue(atomicBoolean.get());

        loader.disableModules(atomicBoolean); //Disableing should put it back to false
        assertFalse(atomicBoolean.get());

        loader.unloadModules();
    }

    private static class AnnotationLoader extends ModuleLoader<AtomicBoolean> {

        public AnnotationLoader(File moduleDirectory) {
            super(moduleDirectory);
        }
    }

    private static class PropertyLoader extends ModuleLoader<AtomicBoolean> {

        public PropertyLoader(File moduleDirectory) {
            super(moduleDirectory, ".jar", new ReflectiveInstanceProvider(), ModulePropertyDescriptor::new);
        }
    }

}