package org.codefx.libfx.collection.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codefx.libfx.collection.transform.ElementTypes.Cat;
import org.codefx.libfx.collection.transform.ElementTypes.Feline;
import org.codefx.libfx.collection.transform.ElementTypes.Mammal;

import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestCollectionGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;

/**
 * Tests {@link TransformingCollection}.
 */
public class TransformingCollectionTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingCollection");
		suite.addTest(backingCollectionHasSupertype());
		suite.addTest(backingCollectionHasSubtype());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'TransformedCollection' passes all calls along,
				// the features are determined by the backing data structure (which is an 'ArrayList')
				CollectionSize.ANY,
				CollectionFeature.ALLOWS_NULL_VALUES, // includes ALLOWS_NULL_QUERIES
				CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				CollectionFeature.KNOWN_ORDER,
				CollectionFeature.SUPPORTS_ADD,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
				CollectionFeature.SUPPORTS_REMOVE,
		};
	}

	/**
	 * Creates a test for a feline collection which us backed by a mammal collection (i.e. a supertype).
	 *
	 * @return the test case
	 */
	private static Test backingCollectionHasSupertype() {
		return CollectionTestSuiteBuilder
				.using(new TransformingCollectionTestGenerator(Mammal.class))
				.named("backed by supertype")
				.withFeatures(features())
				.createTestSuite();
	}

	/**
	 * Creates a test for a feline collection which us backed by a cat collection (i.e. a subtype).
	 *
	 * @return the test case
	 */
	private static Test backingCollectionHasSubtype() {
		return CollectionTestSuiteBuilder
				.using(new TransformingCollectionTestGenerator(Cat.class))
				.named("backed by subtype")
				.withFeatures(features())
				.createTestSuite();
	}

	private static class TransformingCollectionTestGenerator implements TestCollectionGenerator<Feline> {

		private final Class<?> backingSetGenericType;

		public TransformingCollectionTestGenerator(Class<?> backingSetGenericType) {
			this.backingSetGenericType = backingSetGenericType;
		}

		@Override
		public SampleElements<Feline> samples() {
			return new SampleElements<Feline>(
					new Feline("A"), new Feline("B"), new Feline("C"), new Feline("D"), new Feline("E"));
		}

		@Override
		public Feline[] createArray(int length) {
			return new Feline[length];
		}

		@Override
		public Iterable<Feline> order(List<Feline> insertionOrder) {
			return insertionOrder;
		}

		@Override
		public Collection<Feline> create(Object... elements) {
			if (backingSetGenericType.equals(Mammal.class))
				return createBackedByMammal(elements);

			if (backingSetGenericType.equals(Cat.class))
				return createBackedByCat(elements);

			throw new UnsupportedOperationException();
		}

		private static Collection<Feline> createBackedByMammal(Object[] felines) {
			List<Mammal> mammals = new ArrayList<>();
			for (Object feline : felines)
				if (feline == null)
					mammals.add(null);
				else {
					String name = ((Feline) feline).getName();
					mammals.add(new Mammal(name));
				}
			return new TransformingCollection<>(
					mammals,
					/*
					 * Because 'Feline' does not uphold the Liskov Substitution Principle (by having its own 'toString'
					 * method) felines can not masquerade as mammals. Hence create a new mammal for each feline.
					 */
					Mammal.class, Feline.class,
					mammal -> new Feline(mammal.getName()), feline -> new Mammal(feline.getName()));
		}

		private static Collection<Feline> createBackedByCat(Object[] felines) {
			List<Cat> cats = new ArrayList<>();
			for (Object feline : felines)
				if (feline == null)
					cats.add(null);
				else {
					String name = ((Feline) feline).getName();
					cats.add(new Cat(name));
				}
			return new TransformingCollection<>(
					cats,
					/*
					 * Because 'Cat' does not uphold the Liskov Substitution Principle (by having its own 'toString'
					 * method) cats can not masquerade as felines. Hence create a new feline for each cat.
					 */
					Cat.class, Feline.class,
					cat -> new Feline(cat.getName()), feline -> new Cat(feline.getName()));
		}
	}

}
