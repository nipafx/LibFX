package org.codefx.libfx.collection.pool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.codefx.libfx.collection.pool.ResourcePoolStrategy.BorrowInstruction;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.BorrowResult;
import org.codefx.libfx.collection.pool.ResourcePoolStrategy.ForfeitInstruction;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link StrategyBasedResourcePool}.
 */
public class StrategyBasedResourcePoolTest {

	private StrategyBasedResourcePool<Key, PooledResource> pool;

	private ResourceFactory<Key, PooledResource> resourceFactory;

	private ResourcePoolStrategy<Key> strategy;

	@Before
	@SuppressWarnings({ "unchecked", "javadoc" })
	public void createInstances() {
		resourceFactory = mock(ResourceFactory.class);
		strategy = mock(ResourcePoolStrategy.class);
		pool = new StrategyBasedResourcePool<>(resourceFactory, strategy);
	}

	// #region CONSTRUCTION

	@Test
	@SuppressWarnings({ "javadoc", "rawtypes", "unused", "unchecked" })
	public void construction_allArgumentsNonNull_success() {
		new StrategyBasedResourcePool(mock(ResourceFactory.class), mock(ResourcePoolStrategy.class));
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "javadoc", "rawtypes", "unused", "unchecked" })
	public void construction_factoryNull_throwNullPointerException() {
		new StrategyBasedResourcePool(null, mock(ResourcePoolStrategy.class));
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "javadoc", "rawtypes", "unused", "unchecked" })
	public void construction_strategyNull_throwNullPointerException() {
		new StrategyBasedResourcePool(mock(ResourceFactory.class), null);
	}

	// #end CONSTRUCTION

	// #region BORROW

	@Test
	@SuppressWarnings("javadoc")
	public void borrow_nullKey_throwNullPointerException() throws Exception {
		try {
			pool.borrow(null);
			fail();
		} catch (NullPointerException ex) {
			verifyZeroInteractions(resourceFactory, strategy);
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void borrow_createResource_correctInteractionsWithFactoryAndStrategy() throws Exception {
		// let strategy and factory create and return a new resource for "a key"
		Key key = new Key("a key");
		PooledResource resource = new PooledResource("a resource");
		when(strategy.borrowRequest(key)).thenReturn(BorrowInstruction.CREATE);
		when(resourceFactory.createForKey(key)).thenReturn(resource);

		PooledResource borrowedResource = pool.borrow(key).get();

		assertEquals(resource, borrowedResource);
		verify(strategy).borrowRequest(key);
		verify(strategy).borrowed(key, BorrowResult.CREATED);
		verify(resourceFactory).createForKey(key);
		verifyNoMoreInteractions(strategy, resourceFactory);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void borrow_createResourceBecauseQueueEmpty_correctInteractionsWithFactoryAndStrategy() throws Exception {
		// let strategy and factory create and return a new resource for "a key" if the queue us empty (which it should be)
		Key key = new Key("a key");
		PooledResource resource = new PooledResource("a resource");
		when(strategy.borrowRequest(key)).thenReturn(BorrowInstruction.QUERY_QUEUE_AND_IF_EMPTY_CREATE);
		when(resourceFactory.createForKey(key)).thenReturn(resource);

		PooledResource borrowedResource = pool.borrow(key).get();

		assertEquals(resource, borrowedResource);
		verify(strategy).borrowRequest(key);
		verify(strategy).borrowed(key, BorrowResult.CREATED_BECAUSE_QUEUE_EMPTY);
		verify(resourceFactory).createForKey(key);
		verifyNoMoreInteractions(strategy, resourceFactory);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void borrow_returnResourceFromQueue_correctInteractionsWithFactoryAndStrategy() throws Exception {
		// let the strategy return a pooled resource for "a key";
		// to achieve this, first borrow and forfeit a resource for that key
		Key key = new Key("a key");
		PooledResource resource = new PooledResource("a resource");
		when(strategy.borrowRequest(key)).thenReturn(BorrowInstruction.CREATE);
		when(strategy.forfeitRequest(key)).thenReturn(ForfeitInstruction.ADD_TO_QUEUE_AND_IF_FULL_EVICT);
		when(resourceFactory.createForKey(key)).thenReturn(resource);
		pool.borrow(key).forfeit();
		reset(strategy, resourceFactory);

		when(strategy.borrowRequest(key)).thenReturn(BorrowInstruction.QUERY_QUEUE_AND_IF_EMPTY_CREATE);

		PooledResource borrowedResource = pool.borrow(key).get();

		assertEquals(resource, borrowedResource);
		verify(resourceFactory).prepareToBorrow(resource);
		verify(strategy).borrowRequest(key);
		verify(strategy).borrowed(key, BorrowResult.TAKEN_FROM_QUEUE);
		verifyNoMoreInteractions(strategy, resourceFactory);
	}

	// #end BORROW

	// #region NESTED CLASSES

	private static class Key {

		private final String id;

		public Key(String id) {
			assert id != null : "The argument 'id' must not be null.";
			this.id = id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Key))
				return false;

			Key other = (Key) obj;
			return Objects.equals(this.id, other.id);
		}

	}

	private static class PooledResource {

		private final String id;

		public PooledResource(String id) {
			assert id != null : "The argument 'id' must not be null.";
			this.id = id;
		}

	}

	// #end NESTED CLASSES
}
