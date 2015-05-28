package org.codefx.libfx.collection.transform;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.Before;

import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;

/**
 * Tests {@link EqualityTransformingSet}.
 */
public class EqualityTransformingSetTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingSet");
		suite.addTest(originalEquality());
		suite.addTest(lengthBasedEquality());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'EqualityTransformingSet' passes all calls along,
				// the features are determined by the backing data structure (which is a 'HashSet')
				CollectionSize.ANY,
				CollectionFeature.ALLOWS_NULL_VALUES,
				CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				CollectionFeature.SUPPORTS_ADD,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
				CollectionFeature.SUPPORTS_REMOVE,
		};
	}

	/**
	 * Creates a test which uses hashCode and equals of the original keys.
	 *
	 * @return the test case
	 */
	private static Test originalEquality() {
		return SetTestSuiteBuilder
				.using(new TransformingSetGenerator(String::equals, String::hashCode))
				.named("original equality and hashCode")
				.withFeatures(features())
				.createTestSuite();
	}

	/**
	 * Creates a test which uses hashCode and equals based on the string's lengths.
	 *
	 * @return the test case
	 */
	private static Test lengthBasedEquality() {
		BiPredicate<String, String> equals = (s1, s2) -> s1.length() == s2.length();
		ToIntFunction<String> hash = s -> s.length();

		Test generalTests = SetTestSuiteBuilder
				.using(new TransformingSetGenerator(equals, hash))
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
	 * Tests {@link EqualityTransformingSet} with a specific set of tests geared towards its special functionality, i.e.
	 * transforming equals and hashCode.
	 */
	public static class LengthBasedEqualityAndHashCodeTests {

		private Set<String> testedSet;

		private final BiPredicate<String, String> equals = (s1, s2) -> s1.length() == s2.length();

		private final ToIntFunction<String> hash = s -> s.length();

		@Before
		@SuppressWarnings("javadoc")
		public void createSet() {
			testedSet = EqualityTransformingCollectionBuilder
					.forKeyType(String.class)
					.withEquals(equals)
					.withHash(hash)
					.buildSet();
		}

		@org.junit.Test
		@SuppressWarnings("javadoc")
		public void add_containsWithSameLengthElement_true() {
			testedSet.add("aaa");

			assertTrue(testedSet.contains("bbb"));
		}

	}

	private static class TransformingSetGenerator implements TestSetGenerator<String> {

		private final BiPredicate<String, String> equals;
		private final ToIntFunction<String> hash;

		public TransformingSetGenerator(BiPredicate<String, String> equals, ToIntFunction<String> hash) {
			this.equals = equals;
			this.hash = hash;
		}

		@Override
		public Set<String> create(Object... elements) {
			Set<String> transformingSet = EqualityTransformingCollectionBuilder
					.forKeyType(String.class)
					.withEquals(equals)
					.withHash(hash)
					.buildSet();
			Arrays.stream(elements)
					.map(String.class::cast)
					.forEach(transformingSet::add);
			return transformingSet;
		}

		@Override
		public SampleElements<String> samples() {
			return new SampleElements<String>("A", "AA", "AAA", "AAAA", "AAAAA");
		}

		@Override
		public String[] createArray(int length) {
			return new String[length];
		}

		@Override
		public Iterable<String> order(List<String> insertionOrder) {
			return insertionOrder;
		}

	}

}
