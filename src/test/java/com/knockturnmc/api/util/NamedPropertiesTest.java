/*
The MIT License (MIT)

Copyright (c) 2016 Sven Olderaan, http://knockturnmc.com/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 */

package com.knockturnmc.api.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class NamedPropertiesTest {

    @Test
    public void testMapper() throws Exception {
        TestProperties properties = new TestProperties();

        String testString = "test=123\ntest.2=something\ntime.unit=DAYS";
        InputStream stream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));

        properties.load(stream);

        assertEquals(123, properties.test);
        assertEquals(-69, properties.defaulttest);
        assertEquals("something", properties.test2);
        assertEquals(TimeUnit.DAYS, properties.timeUnit);
        assertFalse(properties.booleantest);
        assertEquals(31231232L, properties.someLong);
    }

    private static class TestProperties extends NamedProperties {
        @Property(value = "test")
        int test;

        @Property(value = "booleantest")
        boolean booleantest;

        @Property(value = "defaulttest", defaultvalue = "-69")
        int defaulttest;

        @Property(value = "test.2")
        String test2;

        @Property(value = "time.unit", type = TimeUnit.class)
        TimeUnit timeUnit;

        @Property(value = "some.long", defaultvalue = "31231232")
        long someLong;
    }
}