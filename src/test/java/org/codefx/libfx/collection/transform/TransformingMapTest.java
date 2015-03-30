package org.codefx.libfx.collection.transform;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codefx.libfx.collection.transform.ElementTypes.Cat;
import org.codefx.libfx.collection.transform.ElementTypes.Feline;
import org.codefx.libfx.collection.transform.ElementTypes.Mammal;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.MapFeature;

/**
 * Tests {@link TransformingMap}.
 */
public class TransformingMapTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingMap");
		suite.addTest(backingMapHasSupertype());
		suite.addTest(backingMapHasSubtype());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'TransformedMap' passes all calls along,
				// the features are determined by the backing data structure (which is a 'HashMap')
				CollectionSize.ANY,
				MapFeature.ALLOWS_ANY_NULL_QUERIES,
				MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				MapFeature.SUPPORTS_PUT,
				MapFeature.SUPPORTS_REMOVE,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
		};
	}

	/**
	 * Creates a test for a feline map which us backed by a mammal map (i.e. a supertype).
	 *
	 * @return the test case
	 */
	private static Test backingMapHasSupertype() {
		return MapTestSuiteBuilder
				.using(new TransformingMapGenerator(Mammal.class))
				.named("backed by supertype")
				.withFeatures(features())
				.createTestSuite();
	}

	/**
	 * Creates a test for a feline map which us backed by a cat map (i.e. a subtype).
	 *
	 * @return the test case
	 */
	private static Test backingMapHasSubtype() {
		return MapTestSuiteBuilder
				.using(new TransformingMapGenerator(Cat.class))
				.named("backed by subtype")
				.withFeatures(features())
				.createTestSuite();
	}

	private static class TransformingMapGenerator implements TestMapGenerator<Feline, Feline> {

		private final Class<?> backingMapGenericType;

		public TransformingMapGenerator(Class<?> backingMapGenericType) {
			this.backingMapGenericType = backingMapGenericType;
		}

		@Override
		public SampleElements<Entry<Feline, Feline>> samples() {
			return new SampleElements<Entry<Feline, Feline>>(
					new SimpleEntry<>(new Feline("A"), new Feline("1")),
					new SimpleEntry<>(new Feline("B"), new Feline("2")),
					new SimpleEntry<>(new Feline("C"), new Feline("3")),
					new SimpleEntry<>(new Feline("D"), new Feline("4")),
					new SimpleEntry<>(new Feline("E"), new Feline("5")));
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<Feline, Feline>[] createArray(int length) {
			return new Entry[length];
		}

		@Override
		public Feline[] createKeyArray(int length) {
			return new Feline[length];
		}

		@Override
		public Feline[] createValueArray(int length) {
			return new Feline[length];
		}

		@Override
		public Iterable<Entry<Feline, Feline>> order(List<Entry<Feline, Feline>> insertionOrder) {
			return insertionOrder;
		}

		@Override
		public Map<Feline, Feline> create(Object... entries) {
			if (backingMapGenericType.equals(Mammal.class))
				return createBackedByMammalMap(entries);

			if (backingMapGenericType.equals(Cat.class))
				return createBackedByCatMap(entries);

			throw new UnsupportedOperationException();
		}

		private static Map<Feline, Feline> createBackedByMammalMap(Object[] entries) {
			Map<Mammal, Mammal> mammals = new HashMap<>();
			for (Object entry : entries) {
				String keyName = ((Feline) ((Entry<?, ?>) entry).getKey()).getName();
				String valueName = ((Feline) ((Entry<?, ?>) entry).getValue()).getName();
				mammals.put(new Mammal(keyName), new Mammal(valueName));
			}
			return new TransformingMap<Mammal, Feline, Mammal, Feline>(
					mammals,
					/*
					 * Because 'Feline' does not uphold the Liskov Substitution Principle (by having its own 'toString'
					 * method) felines can not masquerade as mammals. Hence create a new mammal for each feline.
					 */
					Mammal.class, mammal -> new Feline(mammal.getName()),
					Feline.class, feline -> new Mammal(feline.getName()),
					Mammal.class, mammal -> new Feline(mammal.getName()),
					Feline.class, feline -> new Mammal(feline.getName()));
		}

		private static Map<Feline, Feline> createBackedByCatMap(Object[] entries) {
			Map<Cat, Cat> cats = new HashMap<>();
			for (Object entry : entries) {
				String keyName = ((Feline) ((Entry<?, ?>) entry).getKey()).getName();
				String valueName = ((Feline) ((Entry<?, ?>) entry).getValue()).getName();
				cats.put(new Cat(keyName), new Cat(valueName));
			}
			return new TransformingMap<Cat, Feline, Cat, Feline>(
					cats,
					/*
					 * Because 'Cat' does not uphold the Liskov Substitution Principle (by having its own 'toString'
					 * method) cats can not masquerade as felines. Hence create a new feline for each cat.
					 */
					Cat.class, cat -> new Feline(cat.getName()),
					Feline.class, feline -> new Cat(feline.getName()),
					Cat.class, cat -> new Feline(cat.getName()),
					Feline.class, feline -> new Cat(feline.getName()));
		}
	}

}
