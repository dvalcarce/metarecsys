/*
 * Copyright 2016 Information Retrieval Lab - University of A Coruña
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.udc.fi.dc.irlab.metarecsys.structures;

/**
 * The Class MutableInteger.
 *
 * @author daniel.valcarce@udc.es
 */
public class MutableInt {

    /** The value. */
    private int value;

    /**
     * Instantiates a new mutable integer.
     *
     * @param value
     *            the value
     */
    public MutableInt(final int value) {
        this.value = value;
    }

    /**
     * Sets the.
     *
     * @param value
     *            the value
     */
    public void set(final int value) {
        this.value = value;
    }

    /**
     * Get the int value.
     *
     * @return the int
     */
    public int get() {
        return value;
    }

    /**
     * Increment the int value by 1 and return the previous value.
     *
     * @return the previous value
     */
    public int getAndIncrement() {
        return value++;
    }

    /**
     * Increment the int value by 1.
     */
    public void increment() {
        value++;
    }

}