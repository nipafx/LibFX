package org.codefx.libfx.collection.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.codefx.libfx.collection.pool.ResourcePool.Resource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractResourcePoolDemo {

	@BeforeClass
	public static void pauseBeforeTestClass() throws InterruptedException {
		Thread.sleep(0);
	}

	@Before
	public void pauseBeforEachTest() throws InterruptedException {
		Thread.sleep(0);
	}

	@Test
	public void scenarioA_byResources() {
		scenario(Scenario.A, Variant.BY_RESOURCES);
	}

	@Test
	public void scenarioA_byUnitsOfWork() {
		scenario(Scenario.A, Variant.BY_UNITS_OF_WORK);
	}

	@Test
	public void scenarioA_randomized() {
		scenario(Scenario.A, Variant.RANDOMIZED);
	}

	@Test
	public void scenarioX() {
		scenario(Scenario.X, null);
	}

	// #region SCENARIOS

	private void scenario(Scenario scenario, Variant variant) {
		anounceDemo(scenario, variant);

		Statistics stats = run(
				createNewResourcePool(scenario),
				createScenario(scenario, variant));

		System.out.println(stats + "\n");
	}

	private void anounceDemo(Scenario scenario, Variant variant) {
		String variantString = variant == null ? "" : "/" + variant;
		System.out.println(demoName().toUpperCase() + " - Scenario " + scenario + variantString);
	}

	protected enum Scenario {

		/**
		 * Cartesian product of:
		 * <ul>
		 * <li>50 resources with 1MB memory consumption and 50 - 100 ms initialization time
		 * <li>10 units of work per resource with 10 - 20 ms work time
		 * </ul>
		 */
		A,

		/**
		 * Shows the bare pool performance by preventing resource reuse and setting the memory consumption,
		 * initialization and run work time to 0.
		 */
		X,

	}

	protected enum Variant {
		BY_RESOURCES,
		BY_UNITS_OF_WORK,
		RANDOMIZED,
	}

	// create

	private static Queue<UnitOfWork> createScenario(Scenario scenario, Variant variant) {
		switch (scenario) {
			case A:
				return createScenarioA(variant);
			case X:
				return createScenarioX();
			default:
				return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Queue<UnitOfWork> createScenarioA(Variant variant) {
		List[] data;
		switch (variant) {
			case BY_RESOURCES:
				data = createDataForScenarioA();
				return cartesianProductByResources(data[0], data[1]);
			case BY_UNITS_OF_WORK:
				data = createDataForScenarioA();
				return cartesianProductByUnitsOfWork(data[0], data[1]);
			case RANDOMIZED:
				data = createDataForScenarioA();
				return cartesianProductRandomized(data[0], data[1]);
			default:
				return null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected static final List[] createDataForScenarioA() {
		int nrOfResources = 50;
		IntUnaryOperator memoryInKb = resIndex -> 1_000 + resIndex * 10;
		IntToLongFunction initializationTimeMs = resIndex -> 50 + resIndex;

		int nrOfUnitsOfWorkPerResource = 10;
		IntToLongFunction unitWorkTimeMs = workIndex -> 10 + workIndex;

		return new List[] {
				IntStream
						.range(0, nrOfResources)
						.mapToObj(resIndex -> new ResourceKey(
								memoryInKb.applyAsInt(resIndex),
								initializationTimeMs.applyAsLong(resIndex)))
						.collect(Collectors.toList()),
				IntStream.range(0, nrOfUnitsOfWorkPerResource)
						.mapToObj(workIndex -> new UnitOfWorkBlueprint(null, unitWorkTimeMs.applyAsLong(workIndex)))
						.collect(Collectors.toList())
		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Queue<UnitOfWork> createScenarioX() {
		List[] data = createDataForScenarioX();
		return cartesianProductRandomized(data[0], data[1]);
	}

	@SuppressWarnings("rawtypes")
	protected static final List[] createDataForScenarioX() {
		int nrOfResources = 2_000_000;

		return new List[] {
				IntStream
						.range(0, nrOfResources)
						.mapToObj(resIndex -> new ResourceKey(0, 0))
						.collect(Collectors.toList()),
				Collections.singletonList(new UnitOfWorkBlueprint(null, 0))
		};
	}

	protected static final Queue<UnitOfWork> cartesianProductByResources(
			List<ResourceKey> resources, List<UnitOfWorkBlueprint> workBlueprints) {

		Queue<UnitOfWork> workUnits = new ConcurrentLinkedQueue<>();

		int index = 0;
		for (ResourceKey resourceKey : resources)
			for (UnitOfWorkBlueprint blueprint : workBlueprints) {
				UnitOfWork work = new UnitOfWork(resourceKey, blueprint.runTimeInMs, index);
				workUnits.add(work);
				index++;
			}

		return workUnits;
	}

	protected static final Queue<UnitOfWork> cartesianProductByUnitsOfWork(
			List<ResourceKey> resources, List<UnitOfWorkBlueprint> workBlueprints) {

		Queue<UnitOfWork> workUnits = new ConcurrentLinkedQueue<>();

		int index = 0;
		for (UnitOfWorkBlueprint blueprint : workBlueprints)
			for (ResourceKey resourceKey : resources) {
				UnitOfWork work = new UnitOfWork(resourceKey, blueprint.runTimeInMs, index);
				workUnits.add(work);
				index++;
			}

		return workUnits;
	}

	protected static final Queue<UnitOfWork> cartesianProductRandomized(
			List<ResourceKey> resources, List<UnitOfWorkBlueprint> workBlueprints) {

		List<UnitOfWork> workUnits = new ArrayList<>();

		int index = 0;
		for (UnitOfWorkBlueprint blueprint : workBlueprints)
			for (ResourceKey resourceKey : resources) {
				UnitOfWork work = new UnitOfWork(resourceKey, blueprint.runTimeInMs, index);
				workUnits.add(work);
				index++;
			}

		Collections.shuffle(workUnits);
		return new ConcurrentLinkedQueue<>(workUnits);
	}

	// run

	private static Statistics run(
			ResourcePool<ResourceKey, PooledResource> resourcePool, Queue<UnitOfWork> workUnits) {

		if (resourcePool == null)
			return Statistics.ignored();

		ExecutorService executorService = new ThreadPoolExecutor(8, 8, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

		long startTimeInMs = System.currentTimeMillis();

		List<Future<Void>> futures = submit(workUnits, executorService, resourcePool);
		shutdown(executorService);
		assertExceptionFreeTermination(executorService, futures);

		long unitsOfWork = UnitOfWork.workCount.getAndSet(0);
		long createdResources = PooledResource.instanceCount.getAndSet(0);
		return Statistics.completed(unitsOfWork, createdResources, System.currentTimeMillis() - startTimeInMs);
	}

	private static List<Future<Void>> submit(
			Queue<UnitOfWork> workUnits, ExecutorService executorService,
			ResourcePool<ResourceKey, PooledResource> resourcePool) {

		List<Future<Void>> futures = new LinkedList<>();
		for (UnitOfWork unit : workUnits) {
			unit.pool = resourcePool;
			Future<Void> future = executorService.submit(unit);
			futures.add(future);
		}
		return futures;
	}

	private static void shutdown(ExecutorService executorService) {
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
			// should not happen
			throw new RuntimeException(ex);
		}
	}

	private static void assertExceptionFreeTermination(ExecutorService executorService, List<Future<Void>> futures) {
		assert executorService.isTerminated() : "The executor service did not terminate.";
		futures.forEach(future -> {
			assert future.isDone() : "A unit of work was not completed.";
			try {
				future.get();
			} catch (InterruptedException | ExecutionException ex) {
				throw new RuntimeException("Execution of a unit of work failed.", ex);
			}
		});
	}

	// #end SCENARIOS

	// #region ABSTRACT METHODS

	protected abstract String demoName();

	protected abstract ResourcePool<ResourceKey, PooledResource> createNewResourcePool(Scenario scenario);

	// #end ABSTRACT METHODS

	// #region NESTED CLASSES

	protected static final class ResourceKey {

		private final int memoryInKb;

		private final long initializationTimeInMs;

		public ResourceKey(int memoryInKb, long initializationTimeInMs) {
			this.memoryInKb = memoryInKb;
			this.initializationTimeInMs = initializationTimeInMs;
		}
		
		/*
		 * The keys should not override equals and hashCode. The demo relies on the assumption that a pool has to rely
		 * on identity.
		 */

	}

	protected static final class PooledResource {

		private static final AtomicLong instanceCount = new AtomicLong();

		private final long[] data;

		public PooledResource(ResourceKey key) {
			long startTimeInMs = System.currentTimeMillis();

			instanceCount.incrementAndGet();
			int arrayLength = key.memoryInKb * 1024 / 8;
			this.data = new long[arrayLength];

			// waste time
			while (System.currentTimeMillis() - startTimeInMs < key.initializationTimeInMs) {
				// just loop...
			}
		}

	}

	protected static final class UnitOfWorkBlueprint {

		private final ResourceKey resourceKey;

		private final long runTimeInMs;

		public UnitOfWorkBlueprint(ResourceKey resourceKey, long runTimeInMs) {
			this.resourceKey = resourceKey;
			this.runTimeInMs = runTimeInMs;
		}

	}

	private static final class UnitOfWork implements Callable<Void> {

		private static final AtomicLong workCount = new AtomicLong();

		private final ResourceKey resourceKey;

		private final long runTimeInMs;

		private final int index;

		private ResourcePool<ResourceKey, PooledResource> pool;

		public UnitOfWork(ResourceKey resourceKey, long runTimeInMs, int index) {
			this.resourceKey = resourceKey;
			this.runTimeInMs = runTimeInMs;
			this.index = index;
		}

		private void doWork() throws InterruptedException {
			Resource<PooledResource> resource = pool.borrow(resourceKey);

			// waste time
			workCount.incrementAndGet();
			long startTimeInMs = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTimeInMs < runTimeInMs) {
				// just loop...
			}

			pool.forfeit(resource);
		}

		@Override
		public Void call() throws Exception {
			doWork();
			return null;
		}

	}

	private static class Statistics {

		private final long unitsOfWork;

		private final long createdResources;

		private final long elapsedTimeInMs;

		private final boolean ignored;

		private Statistics(long unitsOfWork, long createdResources, long elapsedTimeInMs, boolean ignored) {
			this.unitsOfWork = unitsOfWork;
			this.createdResources = createdResources;
			this.elapsedTimeInMs = elapsedTimeInMs;
			this.ignored = ignored;
		}

		public static Statistics completed(long unitsOfWork, long createdResources, long elapsedTimeInMs) {
			return new Statistics(unitsOfWork, createdResources, elapsedTimeInMs, false);
		}

		public static Statistics ignored() {
			return new Statistics(0, 0, 0, true);
		}

		@Override
		public String toString() {
			return ignored
					? "\t ignored"
					: "\t units of work: " + unitsOfWork + "\n"
							+ "\t created resources: " + createdResources + "\n"
							+ "\t run time: " + elapsedTimeInMs + " ms";
		}
	}

	// #end NESTED CLASSES
}
