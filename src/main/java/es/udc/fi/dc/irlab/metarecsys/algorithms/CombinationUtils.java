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
package es.udc.fi.dc.irlab.metarecsys.algorithms;

import java.util.List;
import java.util.Set;

import net.openhft.koloboke.collect.set.ObjSet;
import net.openhft.koloboke.collect.set.hash.HashObjSets;

/**
 * The Class CombinationUtils.
 *
 * @author daniel.valcarce@udc.es
 */
public final class CombinationUtils {

    /**
     * Get all the combinations (without repetition) of k elements.
     *
     * @param <T>
     *            the generic type of the elements
     * @param elements
     *            the elements
     * @param k
     *            the k
     * @return the iterable
     */
    public static <T> ObjSet<? extends Set<T>> combination(final List<T> elements, final int k) {

        final int n = elements.size();
        if (n < k) {
            throw new IllegalArgumentException("k cannot be bigger than n");
        }

        final int combination[] = new int[k];
        final ObjSet<ObjSet<T>> result = HashObjSets
                .<ObjSet<T>> newUpdatableSet(fact(n) / (fact(k) * fact(n - k)));

        int r = 0;
        int index = 0;

        while (r >= 0) {
            if (index <= (n + (r - k))) {
                combination[r] = index;
                if (r == k - 1) {
                    // if we are at the last position save and increase the
                    // index
                    result.add(createCombination(combination, elements));
                    index++;
                } else {
                    // select index for next position
                    index = combination[r] + 1;
                    r++;
                }
            } else {
                r--;
                if (r > 0) {
                    index = combination[r] + 1;
                } else {
                    index = combination[0] + 1;
                }
            }
        }

        return result;

    }

    /**
     * Create the specified combination of the given elements.
     *
     * @param <T>
     *            the generic type
     * @param combination
     *            the combination
     * @param elements
     *            the elements to combine
     * @return the obj set
     */
    private static <T> ObjSet<T> createCombination(final int[] combination,
            final List<T> elements) {

        final ObjSet<T> resultSet = HashObjSets.<T> newUpdatableSet(combination.length);

        for (final int idx : combination) {
            resultSet.add(elements.get(idx));
        }

        return resultSet;

    }

    /**
     * Compute the factorial of the given number (for a small n).
     *
     * @param n
     *            an positive integer
     * @return the factorial of n
     */
    private static int fact(final int n) {
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

}
