package org.codefx.libfx.collection.pool;

import java.util.function.Function;

public abstract class AbstractStrategyBasedResourcePoolDemo extends AbstractResourcePoolDemo {

	@Override
	protected final ResourcePool<ResourceKey, PooledResource> createNewResourcePool(Scenario scenario) {
		return new StrategyBasedResourcePool<>(createFactory(scenario), createStrategy(scenario));
	}

	private static ResourceFactory<ResourceKey, PooledResource> createFactory(Scenario scenario) {
		switch (scenario) {
			case A:
				return new StaticProcessingTimeResourceFactory(PooledResource::new, 0, 0);
			case X:
				return new StaticProcessingTimeResourceFactory(PooledResource::new, 0, 0);
			default:
				return null;
		}
	}

	protected abstract ResourcePoolStrategy<ResourceKey> createStrategy(Scenario scenario);

	private static class StaticProcessingTimeResourceFactory implements ResourceFactory<ResourceKey, PooledResource> {

		private final Function<ResourceKey, PooledResource> resourceConstructor;

		private final long prepareToBorrowTimeInMs;

		private final long prepareToForfeitTimeInMs;

		public StaticProcessingTimeResourceFactory(
				Function<ResourceKey, PooledResource> resourceConstructor,
				long prepareToBorrowTimeInMs, long prepareToForfeitTimeInMs) {

			this.resourceConstructor = resourceConstructor;
			this.prepareToBorrowTimeInMs = prepareToBorrowTimeInMs;
			this.prepareToForfeitTimeInMs = prepareToForfeitTimeInMs;
		}

		@Override
		public PooledResource createForKey(ResourceKey key) {
			return resourceConstructor.apply(key);
		}

		@Override
		public void prepareToBorrow(PooledResource resource) {
			long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTime < prepareToBorrowTimeInMs) {
				// just loop...
			}
		}

		@Override
		public void prepareToForfeit(PooledResource resource) {
			long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTime < prepareToForfeitTimeInMs) {
				// just loop...
			}
		}

	}

}
