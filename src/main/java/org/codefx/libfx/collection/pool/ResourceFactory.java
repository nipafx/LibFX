package org.codefx.libfx.collection.pool;

/**
 * A factory to create resources from keys.
 * <p>
 * A {@link ResourcePool} implementation which uses a {@code ResourceFactory} will call {@link #prepareToBorrow(Object)
 * prepareToBorrow} before a resource is borrowed, {@link #prepareToForfeit(Object) prepareToForfeit} when it is
 * forfeited and {@link #prepareToEvict(Object) prepareToEvict} before it is evicted.
 * <p>
 * If the {@code ResourcePool} implementation is thread-safe, the factory must be as well. Still, the
 * {@code prepareTo...}-methods will never be called concurrently with the same instance.
 *
 * @param <K>
 *            the type of keys from which resources are created
 * @param <R>
 *            the type of resources created by this factory
 */
public interface ResourceFactory<K, R> {

	/**
	 * Creates a new resource for the specified key.
	 * <p>
	 * The resource must be in a state which allows the pool to immediately hand it out to answer a borrow request. I.e.
	 * if the pool decides to hand out the created instance, it might not call {@link #prepareToBorrow(Object)
	 * prepareToBorrow} prior to that.
	 * <p>
	 * The factory must not reuse instances. Instead, each call must return a new one.
	 *
	 * @param key
	 *            the key with which the resource is associated
	 * @return the new resource
	 */
	R createForKey(K key);

	/**
	 * Prepares the specified resource to be borrowed.
	 * <p>
	 * Called by the {@link ResourcePool} after it was determined that a pooled resource will be borrowed and before the
	 * request is completed and the resource is handed out. Might not be called if the resource was newly
	 * {@link #createForKey(Object) created}.
	 * <p>
	 * Unless a client of the pool violates its contract and continues to use resources after forfeiting them, it is
	 * guaranteed that this resource is not currently used anywhere else.
	 *
	 * @param resource
	 *            the resource to prepare
	 */
	default void prepareToBorrow(@SuppressWarnings("unused") R resource) {
		// by default, do nothing
	}

	/**
	 * Prepares the specified resource to be returned back to the pool.
	 * <p>
	 * Called by the {@link ResourcePool} when a resource is forfeited but before it is added back to the pool or
	 * evicted. Depending on the pool implementation, this method might be called repeatedly with the same resource
	 * before it is successfully forfeited.
	 * <p>
	 * Unless a client of the pool violates its contract and continues to use resources after forfeiting them, it is
	 * guaranteed that this resource is not currently used anywhere else.
	 *
	 * @param resource
	 *            the resource to prepare
	 */
	default void prepareToForfeit(@SuppressWarnings("unused") R resource) {
		// by default, do nothing
	}

	/**
	 * Prepares the specified resource to be evicted from the pool.
	 * <p>
	 * Called by the {@link ResourcePool} after it was determined that a resource will be evicted from the pool. This
	 * might be the case when it is forfeited (in which case this is called after {@link #prepareToForfeit(Object)}) or
	 * at any other time during pool maintenance.
	 * <p>
	 * Unless a client of the pool violates its contract and continues to use resources after forfeiting them, it is
	 * guaranteed that this resource is not currently used anywhere else. It is indeed likely that it will be garbage
	 * collected soon.
	 *
	 * @param resource
	 *            the resource to prepare
	 */
	default void prepareToEvict(@SuppressWarnings("unused") R resource) {
		// by default, do nothing
	}

}
