package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.codefx.libfx.collection.transform.ElementTypes.Cat;
import org.codefx.libfx.collection.transform.ElementTypes.Feline;
import org.codefx.libfx.collection.transform.ElementTypes.Mammal;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;

public class TransformingSetTest {

	public static Test suite() {
		return new TransformingSetTest().allTests();
	}

	public Test allTests() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingSet");
		suite.addTest(testForBackingSetHasSupertype());
		suite.addTest(testForBackingSetHasSubtype());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'TransformedSet' passes all calls along,
				// the features are determined by the backing data structure (which is a 'HashSet')
				CollectionSize.ANY,
				CollectionFeature.ALLOWS_NULL_VALUES, // includes ALLOWS_NULL_QUERIES
				CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				CollectionFeature.SUPPORTS_ADD,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
				CollectionFeature.SUPPORTS_REMOVE,
		};
	}

	public Test testForBackingSetHasSupertype() {
		return SetTestSuiteBuilder
				.using(new TransformingSetGenerator(Mammal.class))
				.named("backed by supertype")
				.withFeatures(features())
				.createTestSuite();
	}

	public Test testForBackingSetHasSubtype() {
		return SetTestSuiteBuilder
				.using(new TransformingSetGenerator(Cat.class))
				.named("backed by subtype")
				.withFeatures(features())
				.createTestSuite();
	}

	private static class TransformingSetGenerator implements TestSetGenerator<Feline> {

		private final Class<?> backingSetGenericType;

		public TransformingSetGenerator(Class<?> backingSetGenericType) {
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
		public Set<Feline> create(Object... elements) {
			if (backingSetGenericType.equals(Mammal.class))
				return createBackedByMammalSet(elements);

			if (backingSetGenericType.equals(Cat.class))
				return createBackedByCatSet(elements);

			throw new UnsupportedOperationException();
		}

		private static Set<Feline> createBackedByMammalSet(Object[] felines) {
			Set<Mammal> mammals = new HashSet<>();
			for (Object feline : felines)
				if (feline == null)
					mammals.add(null);
				else {
					String name = ((Feline) feline).getName();
					mammals.add(new Mammal(name));
				}
			return new TransformingSet<>(
					mammals,
					Mammal.class, mammal -> new Feline(mammal.getName()),
					Feline.class, feline -> new Mammal(feline.getName()));
		}

		private static Set<Feline> createBackedByCatSet(Object[] felines) {
			Set<Cat> cats = new HashSet<>();
			for (Object feline : felines)
				if (feline == null)
					cats.add(null);
				else {
					String name = ((Feline) feline).getName();
					cats.add(new Cat(name));
				}
			return new TransformingSet<>(
					cats,
					Cat.class, cat -> new Feline(cat.getName()),
					Feline.class, feline -> new Cat(feline.getName()));
		}
	}

}
