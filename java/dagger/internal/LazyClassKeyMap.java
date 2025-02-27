/*
 * Copyright (C) 2024 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dagger.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A class keyed map that delegates to a string keyed map under the hood.
 *
 * <p>A {@code LazyClassKeyMap} is created for @LazyClassKey contributed map binding.
 */
public final class LazyClassKeyMap<V> implements Map<Class<?>, V> {
  private final Map<String, V> delegate;

  public static <V> Map<Class<?>, V> of(Map<String, V> delegate) {
    return new LazyClassKeyMap<>(delegate);
  }

  private LazyClassKeyMap(Map<String, V> delegate) {
    this.delegate = delegate;
  }

  @Override
  public V get(Object key) {
    if (!(key instanceof Class)) {
      throw new IllegalArgumentException("Key must be a class");
    }
    return delegate.get(((Class<?>) key).getName());
  }

  @Override
  public Set<Class<?>> keySet() {
    // This method will load all class keys, therefore no need to use @LazyClassKey annotated
    // bindings.
    throw new UnsupportedOperationException(
        "Maps created with @LazyClassKey do not support usage of keySet(). Consider @ClassKey"
            + " instead.");
  }

  @Override
  public Collection<V> values() {
    return delegate.values();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    if (!(key instanceof Class)) {
      throw new IllegalArgumentException("Key must be a class");
    }
    return delegate.containsKey(((Class<?>) key).getName());
  }

  @Override
  public boolean containsValue(Object value) {
    return delegate.containsValue(value);
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public Set<Map.Entry<Class<?>, V>> entrySet() {
    // This method will load all class keys, therefore no need to use @LazyClassKey annotated
    // bindings.
    throw new UnsupportedOperationException(
        "Maps created with @LazyClassKey do not support usage of entrySet(). Consider @ClassKey"
            + " instead.");
  }

  // The dagger map binding should be a immutable map.
  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException("Dagger map bindings are immutable");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("Dagger map bindings are immutable");
  }

  @Override
  public V put(Class<?> key, V value) {
    throw new UnsupportedOperationException("Dagger map bindings are immutable");
  }

  @Override
  public void putAll(Map<? extends Class<?>, ? extends V> map) {
    throw new UnsupportedOperationException("Dagger map bindings are immutable");
  }

  /** A factory for {@code LazyClassKeyMap}. */
  public static class Factory<V> implements Provider<Map<Class<?>, V>> {
    MapFactory<String, V> delegate;

    public static <V> Factory<V> of(MapFactory<String, V> delegate) {
      return new Factory<>(delegate);
    }

    private Factory(MapFactory<String, V> delegate) {
      this.delegate = delegate;
    }

    @Override
    public Map<Class<?>, V> get() {
      return LazyClassKeyMap.of(delegate.get());
    }
  }
}
