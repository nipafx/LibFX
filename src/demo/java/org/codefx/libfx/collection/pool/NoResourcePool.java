package org.codefx.libfx.collection.pool;

import java.util.function.Function;

public class NoResourcePool<K, R> implements ResourcePool<K, R> {

	private final Function<K, R> resourceConstructor;

	public NoResourcePool(Function<K, R> resourceConstructor) {
		this.resourceConstructor = resourceConstructor;
	}

	@Override
	public Resource<R> borrow(K key) throws InterruptedException {
		return new TransparentResource<>(resourceConstructor.apply(key));
	}

	@Override
	public void forfeit(Resource<R> resource) throws IllegalArgumentException {
		// do nothing
	}

	private static class TransparentResource<R> implements Resource<R> {

		private final R resource;

		public TransparentResource(R resource) {
			this.resource = resource;
		}

		@Override
		public void forfeit() {
			// do nothing
		}

		@Override
		public R get() {
			return resource;
		}

	}

}
