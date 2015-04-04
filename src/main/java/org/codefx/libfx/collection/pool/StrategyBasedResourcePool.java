package org.codefx.libfx.collection.pool;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.codefx.libfx.collection.pool.ResourcePoolStrategy.BorrowInstruction;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.BorrowResult;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.ForfeitInstruction;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.ForfeitResult;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.MaintenanceInstruction;

public class StrategyBasedResourcePool<K, R> implements ResourcePool<K, R> {

	private final ConcurrentMap<K, ResourceQueue<K, R>> pool;

	private final ResourceFactory<? super K, R> resourceFactory;

	private final ResourcePoolStrategy<K> strategy;

	public StrategyBasedResourcePool(
			ResourceFactory<? super K, R> resourceFactory, ResourcePoolStrategy<K> strategy) {

		Objects.requireNonNull(resourceFactory, "The argument 'resourceFactory' must not be null.");
		Objects.requireNonNull(strategy, "The argument 'strategy' must not be null.");

		this.pool = new ConcurrentHashMap<>();
		this.resourceFactory = resourceFactory;
		this.strategy = strategy;
	}

	// #region UTILITY METHODS

	private ResourceQueue<K, R> getQueueForKey(K key) {
		return pool.computeIfAbsent(key, this::createQueueForKey);
	}

	private ResourceQueue<K, R> createQueueForKey(K key) {
		// TODO implement more options
		return new UnrestrictedResourceQueue<>();
	}

	// #end UTILITY METHODS

	// #region BORROW

	@Override
	public Resource<R> borrow(K key) throws InterruptedException {
		Objects.requireNonNull(key, "The argument 'key' must not be null.");
		return borrowResource(key);
	}

	private DefaultResource<K, R> borrowResource(K key) throws InterruptedException {
		BorrowInstruction instruction = strategy.borrowRequest(key);

		switch (instruction) {
			case CREATE:
				return answerBorrowByCreate(key);
			case QUERY_QUEUE_AND_IF_EMPTY_CREATE:
				return answerBorrowByQueryPoolAndIfEmptyCreate(key);
			case QUERY_QUEUE_AND_IF_EMPTY_WAIT:
				return answerBorrowByQueryPoolAndIfEmptyWait(key);
			case QUERY_QUEUE_AND_IF_EMPTY_RUN_MAINTENANCE:
				return answerBorrowByQueryPoolAndIfEmptyRunMaintenance(key);
			case RUN_MAINTENANCE:
				return runMaintenanceThenBorrow(key);
			default:
				throw new RuntimeException("Implementation error: unimplemented borrow instruction " + instruction
						+ ".");
		}
	}

	private DefaultResource<K, R> answerBorrowByCreate(K key) {
		R resource = resourceFactory.createForKey(key);
		strategy.borrowed(key, BorrowResult.CREATED);
		return DefaultResource.create(this, key, resource);
	}

	private DefaultResource<K, R> answerBorrowByQueryPoolAndIfEmptyCreate(K key) {
		ResourceQueue<K, R> queueForKey = getQueueForKey(key);
		DefaultResource<K, R> resource = queueForKey.takeNonBlocking();

		boolean poolContainedResource = resource != null;
		if (poolContainedResource) {
			resourceFactory.prepareToBorrow(resource.get());
			strategy.borrowed(key, BorrowResult.TAKEN_FROM_QUEUE);
			return resource;
		} else {
			R res = resourceFactory.createForKey(key);
			strategy.borrowed(key, BorrowResult.CREATED_BECAUSE_QUEUE_EMPTY);
			return DefaultResource.create(this, key, res);
		}
	}

	private DefaultResource<K, R> answerBorrowByQueryPoolAndIfEmptyWait(K key) throws InterruptedException {
		ResourceQueue<K, R> queueForKey = getQueueForKey(key);
		DefaultResource<K, R> resource = queueForKey.takeBlocking();

		resourceFactory.prepareToBorrow(resource.get());
		strategy.borrowed(key, BorrowResult.TAKEN_FROM_QUEUE);
		return resource;
	}

	private DefaultResource<K, R> answerBorrowByQueryPoolAndIfEmptyRunMaintenance(K key) throws InterruptedException {
		ResourceQueue<K, R> queueForKey = getQueueForKey(key);
		DefaultResource<K, R> resource = queueForKey.takeNonBlocking();

		boolean poolContainedResource = resource != null;
		if (poolContainedResource) {
			resourceFactory.prepareToBorrow(resource.get());
			strategy.borrowed(key, BorrowResult.TAKEN_FROM_QUEUE);
			return resource;
		} else
			return runMaintenanceThenBorrow(key);
	}

	private DefaultResource<K, R> runMaintenanceThenBorrow(K key) throws InterruptedException {
		runMaintenancePossiblyBlocking();
		return borrowResource(key);
	}

	// #end BORROW

	// #region FORFEIT

	@Override
	public void forfeit(Resource<R> resource) {
		DefaultResource<K, R> pooledResource = ensureResourceFromThisPool(resource);
		forfeitResource(pooledResource);
	}

	private DefaultResource<K, R> ensureResourceFromThisPool(Resource<R> resource) {
		Objects.requireNonNull(resource, "The argument 'resource' must not be null.");

		try {
			@SuppressWarnings("unchecked")
			// this cast might fail if the caller broke the contract of only forfeiting resources he got from this pool
			DefaultResource<K, R> pooledResource = (DefaultResource<K, R>) resource;
			if (pooledResource.home == this)
				return pooledResource;
			else
				throw new IllegalArgumentException("The forfeited resource does not come from this pool.");
		} catch (ClassCastException ex) {
			throw new IllegalArgumentException("The forfeited resource does not come from this pool.", ex);
		}
	}

	private void forfeitResource(DefaultResource<K, R> resource) {
		resourceFactory.prepareToForfeit(resource.get());
		ForfeitInstruction instruction = strategy.forfeitRequest(resource.key);

		switch (instruction) {
			case EVICT:
				answerForfeitByEvict(resource);
				break;
			case ADD_TO_QUEUE_AND_IF_FULL_EVICT:
				answerForfeitByAddToPoolAndIfFullEvict(resource);
				break;
			case ADD_TO_QUEUE_AND_IF_FULL_RUN_MAINTENANCE:
				answerForfeitByAddToPoolAndIfFullRunMaintenance(resource);
				break;
			case RUN_MAINTENANCE:
				runMaintenanceThenForfeit(resource);
				break;
			default:
				throw new RuntimeException("Implementation error: unimplemented forfeit instruction " + instruction
						+ ".");
		}
	}

	private void answerForfeitByEvict(DefaultResource<K, R> resource) {
		resourceFactory.prepareToEvict(resource.get());
		strategy.forfeited(resource.key, ForfeitResult.EVICTED);
		// for an eviction nothing more needs to be done with the resource; just "forget" the reference
	}

	private void answerForfeitByAddToPoolAndIfFullEvict(DefaultResource<K, R> resource) {
		ResourceQueue<K, R> queueForResource = getQueueForKey(resource.key);
		boolean addedToQueue = queueForResource.addNonBlocking(resource);

		if (addedToQueue)
			strategy.forfeited(resource.key, ForfeitResult.ADDED_TO_QUEUE);
		else {
			resourceFactory.prepareToEvict(resource.get());
			strategy.forfeited(resource.key, ForfeitResult.EVICTED_BECAUSE_QUEUE_FULL);
			// for an eviction nothing more needs to be done with the resource; just "forget" the reference
		}
	}

	private void answerForfeitByAddToPoolAndIfFullRunMaintenance(DefaultResource<K, R> resource) {
		ResourceQueue<K, R> queueForResource = getQueueForKey(resource.key);
		boolean addedToQueue = queueForResource.addNonBlocking(resource);

		if (addedToQueue)
			strategy.forfeited(resource.key, ForfeitResult.ADDED_TO_QUEUE);
		else
			runMaintenanceThenForfeit(resource);
	}

	private void runMaintenanceThenForfeit(DefaultResource<K, R> resource) {
		runMaintenanceNonBlocking();
		forfeitResource(resource);
	}

	// #end FORFEIT

	// #region MAINTENANCE

	private void runMaintenancePossiblyBlocking() throws InterruptedException {
		for (MaintenanceInstruction<K> instruction : strategy.instructMaintenance())
			executeMaintenanceInstruction(instruction, true);
	}

	private void runMaintenanceNonBlocking() {
		try {
			for (MaintenanceInstruction<K> instruction : strategy.instructMaintenance())
				executeMaintenanceInstruction(instruction, false);
		} catch (InterruptedException ex) {
			String message = "Implementation error: maintenance was supposed to be run without blocking but caused InterruptedException.";
			throw new RuntimeException(message, ex);
		}
	}

	private void executeMaintenanceInstruction(MaintenanceInstruction<K> instruction, boolean canBlock)
			throws InterruptedException {
		switch (instruction.function()) {
			case CREATE_RESOURCES:
				addNewResourcesForKeyToQueue(instruction.forKey(), instruction.argument().getAsInt(), false);
				break;
			case CREATE_RESOURCES_EXACTLY:
				addNewResourcesForKeyToQueue(instruction.forKey(), instruction.argument().getAsInt(), true && canBlock);
				break;
			case EVICT_RESOURCES:
				removeResourcesForKeyFromQueue(instruction.forKey(), instruction.argument().getAsInt(), false);
				break;
			case EVICT_RESOURCES_EXACTLY:
				removeResourcesForKeyFromQueue(
						instruction.forKey(), instruction.argument().getAsInt(), true && canBlock);
				break;
			case REMOVE_QUEUE:
				removeQueue(instruction.forKey());
				break;
			default:
				throw new RuntimeException("Implementation error: unimplemented maintenance instruction "
						+ instruction.function() + ".");
		}
	}

	private void addNewResourcesForKeyToQueue(K key, int nrOfResourcesToAdd, boolean canBlock)
			throws InterruptedException {

		ResourceQueue<K, R> queueForKey = getQueueForKey(key);

		// add non blocking as long as possible
		int nrOfAddedResources = addResourcesUntilFull(key, queueForKey::addNonBlocking, nrOfResourcesToAdd);

		// try blocking if necessary and indicated
		int nrOfNotAddedResources = nrOfResourcesToAdd - nrOfAddedResources;
		boolean continueWithBlocking = nrOfNotAddedResources > 0 && canBlock;
		if (continueWithBlocking)
			nrOfAddedResources += addResourcesUntilFull(
					key,
					res -> {
						queueForKey.addBlocking(res);
						return true;
					},
					nrOfNotAddedResources);

		strategy.createdDuringMaintenance(key, nrOfAddedResources);
	}

	private int addResourcesUntilFull(K key, AddToQueue<K, R> add, int nrOfResourcesToAdd) throws InterruptedException {
		int nrOfAddedResources = 0;
		while (nrOfAddedResources < nrOfResourcesToAdd) {
			DefaultResource<K, R> resource = DefaultResource.create(this, key, resourceFactory.createForKey(key));
			boolean added = add.add(resource);
			if (added)
				nrOfAddedResources++;
			else
				return nrOfAddedResources;
		}

		return nrOfAddedResources;
	}

	private void removeResourcesForKeyFromQueue(K key, int nrOfResourcesToRemove, boolean canBlock)
			throws InterruptedException {

		ResourceQueue<K, R> queueForKey = getQueueForKey(key);

		// remove non blocking as long as possible
		int nrOfRemovedResources = removeResourcesUntilNull(queueForKey::takeNonBlocking, nrOfResourcesToRemove);

		// try blocking if necessary and indicated
		int nrOfUnremovedResources = nrOfResourcesToRemove - nrOfRemovedResources;
		boolean continueWithBlocking = nrOfUnremovedResources > 0 && canBlock;
		if (continueWithBlocking)
			nrOfRemovedResources += removeResourcesUntilNull(queueForKey::takeBlocking, nrOfUnremovedResources);

		strategy.evictedDuringMaintenance(key, nrOfRemovedResources);
	}

	private int removeResourcesUntilNull(TakeFromQueue<K, R> take, int nrOfResourcesToRemove)
			throws InterruptedException {

		int nrOfRemovedResources = 0;
		while (nrOfRemovedResources < nrOfResourcesToRemove) {
			DefaultResource<K, R> removedResource = take.take();
			boolean removed = removedResource != null;
			if (removed)
				nrOfRemovedResources++;
			else
				return nrOfRemovedResources;
		}

		return nrOfRemovedResources;
	}

	private void removeQueue(K key) {
		pool.remove(key);
	}

	// #end MAINTENANCE

	// #region NESTED CLASSES

	private interface ResourceQueue<K, R> {

		void setCapacity(int newCapacity);

		boolean addNonBlocking(DefaultResource<K, R> resource);

		void addBlocking(DefaultResource<K, R> resource) throws InterruptedException;

		DefaultResource<K, R> takeNonBlocking();

		DefaultResource<K, R> takeBlocking() throws InterruptedException;

	}

	@SuppressWarnings("serial")
	private static class UnrestrictedResourceQueue<K, R>
			extends LinkedBlockingQueue<DefaultResource<K, R>>
			implements ResourceQueue<K, R> {

		@Override
		public void setCapacity(int newCapacity) {
			throw new UnsupportedOperationException(
					"The capacity of an 'UnrestrictedResourceQueue' can not be set.");
		}

		@Override
		public boolean addNonBlocking(DefaultResource<K, R> resource) {
			return offer(resource);
		}

		@Override
		public void addBlocking(DefaultResource<K, R> resource) throws InterruptedException {
			put(resource);
		}

		@Override
		public DefaultResource<K, R> takeNonBlocking() {
			return poll();
		}

		@Override
		public DefaultResource<K, R> takeBlocking() throws InterruptedException {
			return take();
		}

	}

	@FunctionalInterface
	private interface TakeFromQueue<K, R> {

		DefaultResource<K, R> take() throws InterruptedException;

	}

	@FunctionalInterface
	private interface AddToQueue<K, R> {

		boolean add(DefaultResource<K, R> resource) throws InterruptedException;

	}

	// #end NESTED CLASSES
}
