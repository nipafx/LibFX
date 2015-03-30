package org.codefx.libfx.collection.transform;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.MapFeature;

/**
 * Tests {@link EqualityTransformingMap}.
 */
public class EqualityTransformingMapTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingMap");
		suite.addTest(testForOriginalEquality());
		suite.addTest(testForLengthBasedEquality());
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
	 * Creates a test which uses hashCode and equals of the original keys.
	 *
	 * @return the test case
	 */
	private static Test testForOriginalEquality() {
		return MapTestSuiteBuilder
				.using(new TransformingMapGenerator(String::equals, String::hashCode))
				.named("original equality and hashCode")
				.withFeatures(features())
				.createTestSuite();
	}

	/**
	 * Creates a test which uses hashCode and equals of the original keys.
	 *
	 * @return the test case
	 */
	private static Test testForLengthBasedEquality() {
		BiPredicate<String, String> equal = (s1, s2) -> s1.length() == s2.length();
		ToIntFunction<String> hash = s -> s.length();

		return MapTestSuiteBuilder
				.using(new TransformingMapGenerator(equal, hash))
				.named("original equality and hashCode - Guava tests")
				.withFeatures(features())
				.createTestSuite();

		// TODO test to verify whether transformation actually works and length is the determining factor
	}

	private static class TransformingMapGenerator implements TestMapGenerator<String, Integer> {

		private final BiPredicate<String, String> equals;

		private final ToIntFunction<String> hash;

		public TransformingMapGenerator(BiPredicate<String, String> equals, ToIntFunction<String> hash) {
			this.equals = equals;
			this.hash = hash;
		}

		@Override
		public SampleElements<Entry<String, Integer>> samples() {
			return new SampleElements<Entry<String, Integer>>(
					new SimpleEntry<>("A", 1),
					new SimpleEntry<>("AA", 2),
					new SimpleEntry<>("AAA", 3),
					new SimpleEntry<>("AAAA", 4),
					new SimpleEntry<>("AAAAA", 5));
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<String, Integer>[] createArray(int length) {
			return new Entry[length];
		}

		@Override
		public String[] createKeyArray(int length) {
			return new String[length];
		}

		@Override
		public Integer[] createValueArray(int length) {
			return new Integer[length];
		}

		@Override
		public Iterable<Entry<String, Integer>> order(List<Entry<String, Integer>> insertionOrder) {
			return insertionOrder;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Map<String, Integer> create(Object... entries) {
			Map<String, Integer> transformingMap = EqualityTransformingMap
					.withKeyType(String.class)
					.withInnerMap(() -> new HashMap<Integer, String>())
					.withEquals(equals)
					.withHash(hash)
					.build();

			Arrays.stream(entries)
					.map(entry -> (Entry<String, Integer>) entry)
					.forEach(entry -> transformingMap.put(entry.getKey(), entry.getValue()));

			return transformingMap;
		}
	}

}
