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

public class TransformingCollectionTest {

	public static Test suite() {
		return new TransformingCollectionTest().allTests();
	}

	public Test allTests() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingCollection");
		suite.addTest(testForBackingCollectionHasSupertype());
		suite.addTest(testForBackingCollectionHasSubtype());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// from 'TransformedCollection'
				CollectionFeature.ALLOWS_NULL_QUERIES,
				CollectionFeature.ALLOWS_NULL_VALUES,
				CollectionFeature.SUPPORTS_ADD,
				CollectionFeature.SUPPORTS_REMOVE,
				// from the backing data structure 'ArrayList'
				CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
				CollectionSize.ANY
		};
	}

	public Test testForBackingCollectionHasSupertype() {
		return CollectionTestSuiteBuilder
				.using(new TransformingCollectionTestGenerator(Mammal.class))
				.named("backed by supertype")
				.withFeatures(features())
				.createTestSuite();
	}

	public Test testForBackingCollectionHasSubtype() {
		return CollectionTestSuiteBuilder
				.using(new TransformingCollectionTestGenerator(Cat.class))
				.named("backed by supertype")
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

		private Collection<Feline> createBackedByMammal(Object[] felines) {
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
					Mammal.class, mammal -> new Feline(mammal.getName()),
					Feline.class, feline -> new Mammal(feline.getName()));
		}

		private Collection<Feline> createBackedByCat(Object[] felines) {
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
					Cat.class, cat -> new Feline(cat.getName()),
					Feline.class, feline -> new Cat(feline.getName()));
		}
	}

}
