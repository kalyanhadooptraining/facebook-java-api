package com.facebook.api;

/**
 * Simple data structure for grouping two values together.  Required by some API calls.
 * 
 * @param <N> first element in the pair.
 * @param <V> second element in the pair.
 */
public class Pair<N, V> {
    /**
     * The first element in the pair.
     */
    public N first;
    /**
     * The second element in the pair.
     */
    public V second;

    /**
     * Constructor.
     * 
     * @param name the first value in the pair.
     * @param value the second value in the pair.
     */
    public Pair(N name, V value) {
      this.first = name;
      this.second = value;
    }
}
