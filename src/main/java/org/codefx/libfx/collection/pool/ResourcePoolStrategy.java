package org.codefx.libfx.collection.pool;

import java.util.OptionalInt;

/**
 * A {@code ResourcePoolStrategy} is used by a {@link StrategyBasedResourcePool} to determine how resources are
 * borrowed, forfeited and evicted.
 * <p>
 * The interface consists of three parts: one for borrowing, one for forfeiting and one for maintenance. Each can be
 * divided into two different sets of members: one used to define how the pool will handle a request and another used to
 * inform the strategy how the pool changed as a consequence.
 * <p>
 * The "behavior defining functions" {@link #borrowRequest(Object)} and {@link #forfeitRequest(Object)} are called at
 * some time during the matching client request. If the returned instruction does not include maintenance, the pool will
 * follow the instruction and inform the strategy about the outcome before returning to the calling client.
 * <p>
 * If the instruction included maintenance, the pool will call {@link #instructMaintenance(boolean)} during the same
 * client request. It will also execute all returned instructions during that same request. Afterwards it will request a
 * new instruction for how to answer the initial request.
 * <p>
 * Strategies must be thread-safe: Methods may be called concurrently, calls for different requests may interleave
 * non-deterministically and calls for the same request may come from different threads. All of this can also happen for
 * the same keys.
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

	/**
	 * Requires the strategy to instruct the pool on how to run maintenance.
	 * <p>
	 * Called after the strategy answered a request with {@link BorrowInstruction#RUN_MAINTENANCE},
	 * {@link BorrowInstruction#QUERY_QUEUE_AND_IF_EMPTY_RUN_MAINTENANCE}, {@link ForfeitInstruction#RUN_MAINTENANCE},
	 * or {@link ForfeitInstruction#ADD_TO_QUEUE_AND_IF_FULL_RUN_MAINTENANCE} but before the request returns to the
	 * client. The pool will immediately execute all maintenance instructions. Together this means that maintenance is
	 * run while the client waits for its borrow or forfeit request to finish.
	 * <p>
	 * Since only {@link ResourcePool#borrow(Object) borrow} is allowed to block (
	 * {@link ResourcePool#forfeit(org.codefx.libfx.collection.pool.ResourcePool.Resource) forfeit} is not), blocking
	 * maintenance actions are only valid return values if {@code canBlock} is true.
	 *
	 * @param canBlock
	 *            indicates whether possibly blocking {@link MaintenanceAction}s can be returned
	 * @return the {@link MaintenanceInstruction}s
	 */
	Iterable<MaintenanceInstruction<K>> instructMaintenance(boolean canBlock);

	/**
	 * Informs the strategy that for the specified key the specified number of new resources were added during
	 * maintenance.
	 *
	 * @param key
	 *            the key for which resources were added
	 * @param nrOfAddedResources
	 *            the number of added resources
	 */
	void addedDuringMaintenance(K key, int nrOfAddedResources);

	/**
	 * Informs the strategy that for the specified key the specified number of resources were evicted during
	 * maintenance.
	 *
	 * @param key
	 *            the key for which resources were evicted
	 * @param nrOfEvictedResources
	 *            the number of evicted resources
	 */
	void evictedDuringMaintenance(K key, int nrOfEvictedResources);

	/**
	 * An instruction defines a key and an action (possibly with an integer argument) which should be executed for it.
	 *
	 * @param <K>
	 *            the type of keys used to identify resources
	 */
	interface MaintenanceInstruction<K> {

		/**
		 * @return the key for which the action has to be executed
		 */
		K forKey();

		/**
		 * @return the action to execute
		 */
		MaintenanceAction action();

		/**
		 * @return the argument for the {@link #action()} if applicable
		 */
		OptionalInt argument();
	}

	/**
	 * Defines a maintenance action in an {@link ResourcePoolStrategy.MaintenanceInstruction MaintenanceInstruction}.
	 * <p>
	 * The detailed comments may refer to a "specified key" or a "specified number" - these are the
	 * {@link ResourcePoolStrategy.MaintenanceInstruction#forKey() key} and
	 * {@link ResourcePoolStrategy.MaintenanceInstruction#argument() argument} with which the action is bundled in an
	 * instruction. If these instances are mentioned, they must be provided with the instruction.
	 */
	enum MaintenanceAction {

		/**
		 * Try to create the specified number of resources and add them to the queue for the specified key; stop when
		 * the queue is full.
		 */
		ADD_NEW_RESOURCES,

		/**
		 * Create the specified number of resources and add them to the queue for the specified key; block until all
		 * resources could be added.
		 * <p>
		 * Great care must be taken with this action! If the queue reaches capacity and the client does not continue to
		 * borrow resources, the request which lead to this maintenance action might block forever.
		 */
		ADD_NEW_RESOURCES_POSSIBLY_BLOCKING,

		/**
		 * Try to evict the specified number of resources from the queue for the specified key; stop when the queue is
		 * empty.
		 */
		EVICT_RESOURCES,

		/**
		 * Evict the specified number of resources from the queue for the specified key; block until all resources could
		 * be evicted.
		 * <p>
		 * Great care must be taken with this action! If the queue is emptied and the client does not forfeit borrowed
		 * resources (or none exist), the request which lead to this maintenance action might block forever.
		 */
		EVICT_RESOURCES_POSSIBLY_BLOCKING,

		/**
		 * Remove the queue for the specified key.
		 * <p>
		 * TODO document: unless a more sophisticated implementation is chosen, this might overlap with
		 * borrowing/forfeiting resources for the same key
		 */
		REMOVE_QUEUE,

	}

	// #end MAINTENANCE

}
