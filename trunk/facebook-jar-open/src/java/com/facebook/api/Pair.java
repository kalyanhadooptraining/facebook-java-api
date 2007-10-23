package com.facebook.api;

/**
 * Simple data structure for grouping two values together.  Required by some API calls.
 * 
 * @param <N> first element in the pair.
 * @param <V> second element in the pair.
 */
public class Pair<N, V> {
    public N first;
    public V second;

    public Pair(N name, V value) {
      this.first = name;
      this.second = value;
    }
}
