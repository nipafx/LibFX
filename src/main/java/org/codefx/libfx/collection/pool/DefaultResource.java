package org.codefx.libfx.collection.pool;

import org.codefx.libfx.collection.pool.ResourcePool.Resource;

/**
 * A {@link Resource} which holds references to the pool from which it was borrowed and the key with which it is
 * associated.
 *
 * @param <K>
 *            the type of the key with which this resource is identified
 * @param <R>
 *            the type of the wrapped resources
 */
final class DefaultResource<K, R> implements Resource<R> {

	/**
	 * The pool from which this resource was borrowed.
	 */
	final ResourcePool<K, R> home;

	/**
	 * The key which with this resource is identified
	 */
	final K key;

	/**
	 * The actual resource.
	 */
	final R resource;

	private DefaultResource(ResourcePool<K, R> home, K key, R resource) {
		assert home != null : "The argument 'home' must not be null.";
		assert key != null : "The argument 'key' must not be null.";
		assert resource != null : "The argument 'resource' must not be null.";

		this.home = home;
		this.key = key;
		this.resource = resource;
	}

	/**
	 * Creates a new resource.
	 *
	 * @param home
	 *            the pool from which this resource was borrowed
	 * @param key
	 *            the key with which this resource is associated
	 * @param resource
	 *            the actual resource
	 * @return a {@code DefaultResource} which contains the specified arguments
	 * @param <K>
	 *            the type of the key with which this resource is identified
	 * @param <R>
	 *            the type of the wrapped resources
	 */
	public static <K, R> DefaultResource<K, R> create(ResourcePool<K, R> home, K key, R resource) {
		return new DefaultResource<>(home, key, resource);
	}

	@Override
	public void forfeit() {
		home.forfeit(this);
	}

	@Override
	public R get() {
		return resource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + home.hashCode();
		result = prime * result + key.hashCode();
		result = prime * result + resource.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefaultResource))
			return false;

		@SuppressWarnings("rawtypes")
		DefaultResource other = (DefaultResource) obj;
		return this.home == other.home
				&& this.key == other.key
				&& this.resource == other.resource;
	}

}
