package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;

import org.codefx.libfx.collection.transform.ElementTypes.Cat;
import org.codefx.libfx.collection.transform.ElementTypes.Feline;
import org.codefx.libfx.collection.transform.ElementTypes.Mammal;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;

public class TransformingSetTest {

	public static Test suite() {
		return new TransformingSetTest().testForBackingSetHasSupertype();
	}

	public Test testForBackingSetHasSupertype() {
		return SetTestSuiteBuilder
				.using(new TransformingSetGenerator(Mammal.class))
				.named("supertype")
				.withFeatures(
						// from 'TransformedSet'
						CollectionFeature.ALLOWS_NULL_QUERIES,
						CollectionFeature.ALLOWS_NULL_VALUES,
						CollectionFeature.SUPPORTS_ADD,
						CollectionFeature.SUPPORTS_REMOVE,
						// from the backing data structure 'HashSet'
						CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
						CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
						CollectionSize.ANY)
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

		private Set<Feline> createBackedByMammalSet(Object[] felines) {
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
					Feline.class, feline -> feline);
		}

		private Set<Feline> createBackedByCatSet(Object[] felines) {
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
					Cat.class, cat -> cat,
					Feline.class, feline -> new Cat(feline.getName()));
		}
	}

}
