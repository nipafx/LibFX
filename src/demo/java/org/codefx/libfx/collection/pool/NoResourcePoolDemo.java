package org.codefx.libfx.collection.pool;

public class NoResourcePoolDemo extends AbstractResourcePoolDemo {

	@Override
	protected String demoName() {
		return "No Resource Pool";
	}

	@Override
	protected ResourcePool<ResourceKey, PooledResource> createNewResourcePool(Scenario scenario) {
		return new NoResourcePool<>(PooledResource::new);
	}

}
