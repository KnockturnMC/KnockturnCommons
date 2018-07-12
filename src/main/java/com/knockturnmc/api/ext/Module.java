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

package com.knockturnmc.api.ext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a module that can be loaded can use to extend the base of an application
 *
 * @see Loadable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {

    /**
     * The name of the module
     *
     * @return the name
     */
    String name();

    /**
     * The author of the module
     *
     * @return the author
     */
    String author();

    /**
     * Specifies if this module is reloadable
     *
     * @return reloadable
     */
    boolean reloadable() default true;

    /**
     * Specifies the version of this module
     *
     * @return the version
     */
    String version() default "";

    /**
     * Returns the dependencies for the given module
     *
     * @return the dependencie ids
     */
    String[] dependencies() default {};
}