package org.codefx.libfx.collection.pool;

/**
 * A resource pool holds objects (called resources) for reuse.
 * <p>
 * A pool is used to prevent the repeated creation of resources; typically in cases where construction is expensive.
 * Resources are instead taken from the pool, used and then returned to it, which stores them for later requests.
 * <p>
 * Keys are used to identify resources. All resources which are identified by the same key (in terms of {@code equals})
 * are treated as completely interchangeable.
 * <p>
 * A resource is borrowed by calling {@link #borrow(Object) borrow(Key)}. This might either return a pooled resource,
 * create a new one or block until a resource becomes available. The concrete behavior depends on the chosen
 * implementation and the state of the pool. A resource can be forfeited either by calling {@link #forfeit(Resource)} on
 * this pool, {@link Resource#forfeit() forfeit()} on the resource or enclosing it in a <a
 * href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resource</a>
 * block.
 * <p>
 * After being forfeited, resources must not be used until borrowed again. The pool guarantees that the same resource
 * instance is not borrowed twice without forfeiting it in between. Together, this ensures that a resource can not be
 * used by more than one client at a time. Resources must only be forfeited to the same pool. Violating this requirement
 * might not be detectable at runtime and can lead to unexpected behavior. Implementations might require that each
 * borrowed resource is eventually forfeited.
 * <p>
 * The pool might (and most likely will) employ some kind of eviction strategy to prevent memory leaks and
 * ever-increasing memory use. Whether and how the size of the pool is managed depends on the implementation.
 * <p>
 * Pools might or might not be thread-safe. If they are, all of the guarantees also hold under concurrent use by
 * multiple threads.
 *
 * @param <K>
 *            the type of keys used to identify resources
 * @param <R>
 *            the type of resources maintained by this pool
 */
public interface ResourcePool<K, R> {

	/**
	 * Borrows a resource which is identified by the specified key.
	 * <p>
	 * Until the resource is {@link #forfeit(Resource) forfeited}, no other call to this method will return the same
	 * instance.
	 * <p>
	 * Depending on the implementation, this call might block until a resource becomes available. It might also be
	 * required to eventually forfeit the resource.
	 *
	 * @param key
	 *            the key for which a resource is requested
	 * @return the resource
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */
	Resource<R> borrow(K key) throws InterruptedException;

	/**
	 * Returns the specified resource to this pool.
	 * <p>
	 * Depending on the implementation, this method may throw an {@link IllegalArgumentException}, if the resource does
	 * not come from this pool. It might also be required to eventually call this for each borrowed resource.
	 *
	 * @param resource
	 *            the resource to return to this pool
	 * @throws IllegalArgumentException
	 *             if it could be detected that the resource does not come from this pool
	 */
	void forfeit(Resource<R> resource) throws IllegalArgumentException;

	/**
	 * Wraps a resource which was borrowed from a {@link ResourcePool}.
	 * <p>
	 * Calling {@link #get()} accesses the actual resource, {@link #forfeit()} returns it to the pool. Alternatively,
	 * this instance can be used in a <a
	 * href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">try-with-resource</a>
	 * block (it implements {@link AutoCloseable} where {@link #close()} calls {@code forfeit()}) which guarantees that
	 * it will be returned to the pool when the block is exited.
	 * <p>
	 * Depending on the implementation of {@code ResourcePool} it might be required to eventually forfeit this resource.
	 * It is also possible that resources hold a reference to the pool, so holding on to them might keep the pool from
	 * getting garbage collected.
	 *
	 * @param <R>
	 *            the type of the wrapped resources
	 */
	interface Resource<R> extends AutoCloseable {

		@Override
		default void close() {
			forfeit();
		}

		/**
		 * Returns this resource to the pool from which it came.
		 */
		void forfeit();

		/**
		 * @return the resource
		 */
		R get();

	}
}
