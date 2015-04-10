package org.codefx.libfx.collection.transform;

import static org.junit.Assert.assertEquals;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.Before;

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
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.EqualityTransformingMap");
		suite.addTest(originalEquality());
		suite.addTest(lengthBasedEquality());
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
	private static Test originalEquality() {
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
	private static Test lengthBasedEquality() {
		BiPredicate<String, String> equals = (s1, s2) -> s1.length() == s2.length();
		ToIntFunction<String> hash = s -> s.length();

		Test generalTests = MapTestSuiteBuilder
				.using(new TransformingMapGenerator(equals, hash))
				.named("length-based equality and hashCode - general tests")
				.withFeatures(features())
				.createTestSuite();
		TestSuite specificTests = new TestSuite("length-based equality and hashCode - specific tests");
		specificTests.addTest(new JUnit4TestAdapter(LengthBasedEqualityAndHashCodeTests.class));

		TestSuite tests = new TestSuite("length-based equality and hashCode");
		tests.addTest(generalTests);
		tests.addTest(specificTests);
		return tests;
	}

	/**
	 * Tests {@link EqualityTransformingMap} with a specific set of tests geared towards its special functionality, i.e.
	 * transforming equals and hashCode.
	 */
	public static class LengthBasedEqualityAndHashCodeTests {

		private Map<String, Integer> testedMap;

		private final BiPredicate<String, String> equals = (s1, s2) -> s1.length() == s2.length();

		private final ToIntFunction<String> hash = s -> s.length();

		@Before
		@SuppressWarnings("javadoc")
		public void createMap() {
			testedMap = EqualityTransformingMap
					.withKeyType(String.class)
					.withInnerMap(() -> new HashMap<Integer, String>())
					.withEquals(equals)
					.withHash(hash)
					.build();
		}

		@org.junit.Test
		@SuppressWarnings("javadoc")
		public void put_getWithSameLengthKey_exists() {
			Integer associatedValue = 1000;
			testedMap.put("aaa", associatedValue);

			assertEquals(associatedValue, testedMap.get("bbb"));
		}

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
