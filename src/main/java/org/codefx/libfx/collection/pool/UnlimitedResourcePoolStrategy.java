package org.codefx.libfx.collection.pool;

import java.util.ArrayList;

/**
 * A {@link ResourcePoolStrategy} which assumes an unlimited pool.
 * <p>
 * Borrow requests are fulfilled by taking resources from the pool or, if none are available, creating new ones.
 * Forfeited resources are always added back to the pool. There is no maintenance.
 * <p>
 * It is not necessary to forfeit borrowed resources.
 *
 * @param <K>
 *            the type of keys used to identify resources
 */
public class UnlimitedResourcePoolStrategy<K> implements ResourcePoolStrategy<K> {

	@Override
	public BorrowInstruction borrowRequest(K key) {
		return BorrowInstruction.QUERY_QUEUE_AND_IF_EMPTY_CREATE;
	}

	@Override
	public void borrowed(K key, BorrowResult result) {
		// do nothing
	}

	@Override
	public ForfeitInstruction forfeitRequest(K key) {
		return ForfeitInstruction.ADD_TO_QUEUE_AND_IF_FULL_EVICT;
	}

	@Override
	public void forfeited(K key, ForfeitResult result) {
		// do nothing
	}

	@Override
	public Iterable<MaintenanceInstruction<K>> instructMaintenance(boolean canBlock) {
		// do nothing
		return new ArrayList<>();
	}

	@Override
	public void addedDuringMaintenance(K key, int nrOfCreatedResources) {
		// do nothing
	}

	@Override
	public void evictedDuringMaintenance(K key, int nrOfEvictedResources) {
		// do nothing
	}

}
