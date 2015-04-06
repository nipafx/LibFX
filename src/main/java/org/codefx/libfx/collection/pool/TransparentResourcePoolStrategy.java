package org.codefx.libfx.collection.pool;

import java.util.ArrayList;

/**
 * A {@link ResourcePoolStrategy} which acts as if the pool were non-existent.
 * <p>
 * Borrow requests are fulfilled by creating new resources. Forfeited resources are never added to the pool. There is no
 * maintenance.
 * <p>
 * It is not necessary to forfeit borrowed resources. But it should be noted that resources can hold a reference to the
 * pool which might prevent garbage collection.
 *
 * @param <K>
 *            the type of keys used to identify resources
 */
public class TransparentResourcePoolStrategy<K> implements ResourcePoolStrategy<K> {

	@Override
	public BorrowInstruction borrowRequest(K key) {
		return BorrowInstruction.CREATE;
	}

	@Override
	public void borrowed(K key, BorrowResult result) {
		// do nothing
	}

	@Override
	public ForfeitInstruction forfeitRequest(K key) {
		return ForfeitInstruction.EVICT;
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
