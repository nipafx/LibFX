package org.codefx.libfx.collection.pool;

public class TransparentResourcePoolStrategyDemo extends AbstractStrategyBasedResourcePoolDemo {

	@Override
	protected String demoName() {
		return "Transparent Resource Pool";
	}

	@Override
	protected ResourcePoolStrategy<ResourceKey> createStrategy(Scenario scenario) {
		return new TransparentResourcePoolStrategy<>();
	}

}
