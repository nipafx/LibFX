package org.codefx.libfx.collection.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.common.base.Objects;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.ListFeature;

/**
 * Tests {@link OptionalTransformingList}.
 */
public class OptionalTransformingListTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingList");
		suite.addTest(optionalWithNullDefaultValue());
		suite.addTest(optionalWithNonNullDefaultValue());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'OptionalTransformingList' passes all calls along,
				// the features are determined by the backing data structure (which is an 'ArrayList')
				CollectionSize.ANY,
				// exclude 'CollectionFeature.ALLOWS_NULL_VALUES' because nulls are handled in a special way
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
	 * Creates a test for a set which us backed by a collection of {@link Optional Optional&lt;String&gt;}. The empty
	 * Optional is represented with null.
	 *
	 * @return the test case
	 */
	private static Test optionalWithNullDefaultValue() {
		return ListTestSuiteBuilder
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
	private static Test optionalWithNonNullDefaultValue() {
		return ListTestSuiteBuilder
				.using(new OptionalTestGenerator("DEFAULT"))
				.named("Optional<String> with 'DEFAULT' as default")
				// if null is not the default value, the set does not allow null values
				.withFeatures(features())
				.createTestSuite();
	}

	private static class OptionalTestGenerator implements TestListGenerator<String> {

		private final String defaultValue;

		public OptionalTestGenerator(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public SampleElements<String> samples() {
			return new SampleElements<String>("A", "B", "C", "D", "E");
		}

		@Override
		public List<String> create(Object... elements) {
			List<Optional<String>> optionalStrings = createListOfOptionalStrings(elements);
			return new OptionalTransformingList<>(optionalStrings, String.class, defaultValue);
		}

		private List<Optional<String>> createListOfOptionalStrings(Object... elements) {
			List<Optional<String>> optionalStrings = new ArrayList<>();
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
