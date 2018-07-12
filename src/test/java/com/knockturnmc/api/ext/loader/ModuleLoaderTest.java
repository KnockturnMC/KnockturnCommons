package com.knockturnmc.api.ext.loader;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ModuleLoaderTest {

    @Test
    public void testLoader() {
        File moduleFile = new File(System.getProperty("user.dir") + "/src/test/resources");
        TestLoader loader = new TestLoader(moduleFile);

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        loader.loadModules();

        loader.enableModules(atomicBoolean);
        assertEquals(1, loader.getLoadedModules().size());
        assertTrue(atomicBoolean.get());

        loader.disableModules(atomicBoolean); //Disableing should put it back to false
        assertFalse(atomicBoolean.get());
    }

    private class TestLoader extends ModuleLoader<AtomicBoolean> {

        public TestLoader(File moduleDirectory) {
            super(moduleDirectory);
        }
    }

}