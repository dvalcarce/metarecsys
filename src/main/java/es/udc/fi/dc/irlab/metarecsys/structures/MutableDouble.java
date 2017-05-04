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
 * The Class MutableDouble.
 *
 * @author daniel.valcarce@udc.es
 */
public class MutableDouble {

    /** The value. */
    private double value;

    /**
     * Instantiates a new mutable double.
     *
     * @param value
     *            the value
     */
    public MutableDouble(final double value) {
        this.value = value;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the value
     */
    public void set(final double value) {
        this.value = value;
    }

    /**
     * Get the double value.
     *
     * @return the double
     */
    public double get() {
        return value;
    }

    /**
     * Increment the value.
     *
     * @param inc
     *            the increment
     */
    public void add(final double inc) {
        this.value += inc;
    }

}