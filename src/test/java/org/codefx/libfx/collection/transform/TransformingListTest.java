package org.codefx.libfx.collection.transform;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codefx.libfx.collection.transform.ElementTypes.Cat;
import org.codefx.libfx.collection.transform.ElementTypes.Feline;
import org.codefx.libfx.collection.transform.ElementTypes.Mammal;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.ListFeature;

/**
 * Tests {@link TransformingCollection}.
 */
public class TransformingListTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingCollection");
		suite.addTest(backingListHasSupertype());
		suite.addTest(backingListHasSubtype());
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
				ListFeature.SUPPORTS_ADD_WITH_INDEX,
				ListFeature.SUPPORTS_SET,
				ListFeature.SUPPORTS_REMOVE_WITH_INDEX,
		};
	}

	/**
	 * Creates a test for a feline list which us backed by a mammal list (i.e. a supertype).
	 *
	 * @return the test case
	 */
	private static Test backingListHasSupertype() {
		return ListTestSuiteBuilder
				.using(new TransformingListTestGenerator(Mammal.class))
				.named("backed by supertype")
				.withFeatures(features())
				.createTestSuite();
	}

	/**
	 * Creates a test for a feline list which us backed by a cat list (i.e. a subtype).
	 *
	 * @return the test case
	 */
	private static Test backingListHasSubtype() {
		return ListTestSuiteBuilder
				.using(new TransformingListTestGenerator(Cat.class))
				.named("backed by subtype")
				.withFeatures(features())
				.createTestSuite();
	}

	private static class TransformingListTestGenerator implements TestListGenerator<Feline> {

		private final Class<?> backingSetGenericType;

		public TransformingListTestGenerator(Class<?> backingSetGenericType) {
			this.backingSetGenericType = backingSetGenericType;
		}

		@Override
		public SampleElements<Feline> samples() {
			return new SampleElements<>(
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
		public List<Feline> create(Object... elements) {
			if (backingSetGenericType.equals(Mammal.class))
				return createBackedByMammal(elements);

			if (backingSetGenericType.equals(Cat.class))
				return createBackedByCat(elements);

			throw new UnsupportedOperationException();
		}

		private static List<Feline> createBackedByMammal(Object[] felines) {
			List<Mammal> mammals = new ArrayList<>();
			for (Object feline : felines)
				if (feline == null)
					mammals.add(null);
				else {
					String name = ((Feline) feline).getName();
					mammals.add(new Mammal(name));
				}
			return new TransformingList<>(
					mammals,
					/*
					 * Because 'Feline' does not uphold the Liskov Substitution Principle (by having its own 'toString'
					 * method) felines can not masquerade as mammals. Hence create a new mammal for each feline.
					 */
					Mammal.class, Feline.class,
					mammal -> new Feline(mammal.getName()), feline -> new Mammal(feline.getName()));
		}

		private static List<Feline> createBackedByCat(Object[] felines) {
			List<Cat> cats = new ArrayList<>();
			for (Object feline : felines)
				if (feline == null)
					cats.add(null);
				else {
					String name = ((Feline) feline).getName();
					cats.add(new Cat(name));
				}
			return new TransformingList<>(
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
