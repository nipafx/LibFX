package org.codefx.libfx.collection.pool;

import java.util.OptionalInt;

/**
 * A {@code ResourcePoolStrategy} is used by a {@link StrategyBasedResourcePool} to determine how resources are
 * burrowed, forfeited and pooled.
 * <p>
 * TODO
 * <p>
 * If the {@code ResourcePool} implementation is thread-safe, the strategy must be as well.
 *
 * @param <K>
 *            the type of keys used to identify resources
 */
public interface ResourcePoolStrategy<K> {

	// #region BORROW

	/**
	 * Requires the strategy to instruct the pool on how to fulfill a borrow request for a resource identified by the
	 * specified key.
	 *
	 * @param key
	 *            the key which identifies the requested resource
	 * @return the instruction of how to fulfill the request
	 */
	BorrowInstruction borrowRequest(K key);

	/**
	 * Informs the strategy that a resource was borrowed.
	 *
	 * @param key
	 *            the key which identifies the borrowed resource
	 * @param result
	 *            indicates how the request was fulfilled
	 */
	void borrowed(K key, BorrowResult result);

	/**
	 * Instructs the pool on how to fulfill a borrow request.
	 */
	enum BorrowInstruction {

		/**
		 * Return a newly created resource.
		 */
		CREATE,

		/**
		 * Try to return a resource from the queue; if none are available, create a new one.
		 */
		QUERY_QUEUE_AND_IF_EMPTY_CREATE,

		/**
		 * Return a resource from the queue, waiting if necessary until one becomes available.
		 */
		QUERY_QUEUE_AND_IF_EMPTY_WAIT,

		/**
		 * Try to return a resource from the queue; if none are available, run maintenance and request a new instruction
		 * afterwards.
		 */
		QUERY_QUEUE_AND_IF_EMPTY_RUN_MAINTENANCE,

		/**
		 * Run maintenance and request a new instruction afterwards.
		 */
		RUN_MAINTENANCE
	}

	/**
	 * Informs the strategy how the pool is fulfilling a borrow request.
	 */
	enum BorrowResult {

		/**
		 * Returns a newly created resource.
		 */
		CREATED,

		/**
		 * Returns a newly created resource because the pool was empty.
		 */
		CREATED_BECAUSE_QUEUE_EMPTY,

		/**
		 * Returns a resource which was taken from the queue.
		 */
		TAKEN_FROM_QUEUE,
	}

	// #end BORROW

	// #region FORFEIT

	/**
	 * Requires the strategy to instruct the pool on how to fulfill a forfeit request for a resource identified by the
	 * specified key.
	 *
	 * @param key
	 *            the key which identifies the forfeited resource
	 * @return the instruction of how to fulfill the request
	 */
	ForfeitInstruction forfeitRequest(K key);

	/**
	 * Informs the strategy that a resource was forfeited.
	 *
	 * @param key
	 *            the key which identifies the forfeited resource
	 * @param result
	 *            indicates how the request was fulfilled
	 */
	void forfeited(K key, ForfeitResult result);

	/**
	 * Instructs the pool on how to fulfill a forfeit request.
	 */
	enum ForfeitInstruction {

		/**
		 * Evict the resource from the pool.
		 */
		EVICT,

		/**
		 * Try to add the resource to the queue; if it is full, evict the resource.
		 */
		ADD_TO_QUEUE_AND_IF_FULL_EVICT,

		/**
		 * Try to add the resource to the queue; if it is full, run maintenance and request a new instruction
		 * afterwards.
		 */
		ADD_TO_QUEUE_AND_IF_FULL_RUN_MAINTENANCE,

		/**
		 * Run maintenance and request a new instruction afterwards.
		 */
		RUN_MAINTENANCE,

	}

	/**
	 * Informs the strategy how the pool is fulfilling a forfeit request.
	 */
	enum ForfeitResult {

		/**
		 * The resource is evicted from the pool.
		 */
		EVICTED,

		/**
		 * The resource is evicted from the pool because the queue was full.
		 */
		EVICTED_BECAUSE_QUEUE_FULL,

		/**
		 * The resource is added to the queue.
		 */
		ADDED_TO_QUEUE

	}

	// #end FORFEIT

	// #region MAINTENANCE

	Iterable<MaintenanceInstruction<K>> instructMaintenance();

	void createdDuringMaintenance(K key, int nrOfCreatedResources);

	void evictedDuringMaintenance(K key, int nrOfEvictedResources);

	interface MaintenanceInstruction<K> {

		K forKey();

		MaintenanceFunction function();

		OptionalInt argument();
	}

	// TODO rename ?
	enum MaintenanceFunction {

		CREATE_RESOURCES,

		CREATE_RESOURCES_EXACTLY,

		EVICT_RESOURCES,

		EVICT_RESOURCES_EXACTLY,

		/*
		 * TODO document: unless a more sophisticated implementation is chosen, this might overlap with
		 * borrowing/forfeiting resources for the same key
		 */
		REMOVE_QUEUE,

	}

	// #end MAINTENANCE

}
