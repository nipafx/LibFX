package org.codefx.libfx.collection.pool;


public class UnlimitedResourcePoolStrategyDemo extends AbstractStrategyBasedResourcePoolDemo {

	@Override
	protected String demoName() {
		return "Unlimited Resource Pool";
	}

	@Override
	protected ResourcePoolStrategy<ResourceKey> createStrategy(Scenario scenario) {
		return new UnlimitedResourcePoolStrategy<>();
	}

}
