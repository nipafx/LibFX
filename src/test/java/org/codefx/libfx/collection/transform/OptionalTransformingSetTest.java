package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.common.base.Objects;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;

/**
 * Tests {@link OptionalTransformingSet}.
 */
public class OptionalTransformingSetTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingSet");
		suite.addTest(testForOptionalWithNullDefaultValue());
		suite.addTest(testForOptionalWithNonNullDefaultValue());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'OptionalTransformingSet' passes all calls along,
				// the features are determined by the backing data structure (which is a 'HashSet')
				CollectionSize.ANY,
				// exclude 'CollectionFeature.ALLOWS_NULL_VALUES' because nulls are handled in a special way
				CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				CollectionFeature.KNOWN_ORDER,
				CollectionFeature.SUPPORTS_ADD,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
				CollectionFeature.SUPPORTS_REMOVE,
		};
	}

	/**
	 * Creates a test for a set which us backed by a collection of {@link Optional Optional&lt;String&gt;}. The empty
	 * Optional is represented with null.
	 *
	 * @return the test case
	 */
	private static Test testForOptionalWithNullDefaultValue() {
		return SetTestSuiteBuilder
				.using(new OptionalTestGenerator(null))
				.named("Optional<String> with null as default")
				.withFeatures(features())
				// if null is the default value, the collection allows null values
				.withFeatures(CollectionFeature.ALLOWS_NULL_VALUES)
				.createTestSuite();
	}

	/**
	 * Creates a test for a set which us backed by a collection of {@link Optional Optional&lt;String&gt;}. The empty
	 * Optional is represented with "DEFAULT".
	 *
	 * @return the test case
	 */
	private static Test testForOptionalWithNonNullDefaultValue() {
		return SetTestSuiteBuilder
				.using(new OptionalTestGenerator("DEFAULT"))
				.named("Optional<String> with 'DEFAULT' as default")
				// if null is not the default value, the set does not allow null values
				.withFeatures(features())
				.createTestSuite();
	}

	private static class OptionalTestGenerator implements TestSetGenerator<String> {

		private final String defaultValue;

		public OptionalTestGenerator(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public SampleElements<String> samples() {
			return new SampleElements<String>("A", "B", "C", "D", "E");
		}

		@Override
		public Set<String> create(Object... elements) {
			Set<Optional<String>> optionalStrings = createSetOfOptionalStrings(elements);
			return new OptionalTransformingSet<>(optionalStrings, String.class, defaultValue);
		}

		private Set<Optional<String>> createSetOfOptionalStrings(Object... elements) {
			Set<Optional<String>> optionalStrings = new HashSet<>();
			for (Object element : elements) {
				String string = (String) element;
				optionalStrings.add(
						Objects.equal(string, defaultValue)
								? Optional.empty()
								: Optional.of(string));
			}
			return optionalStrings;
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
