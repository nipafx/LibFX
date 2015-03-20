package org.codefx.libfx.collection.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.common.base.Objects;
import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestCollectionGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;

/**
 * Tests {@link OptionalTransformingCollection}.
 */
public class OptionalTransformingCollectionTest {

	/**
	 * JUnit-3-style method to create the tests run for this class.
	 *
	 * @return the tests to run
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("org.codefx.libfx.collection.transform.TransformingCollection");
		suite.addTest(testForObjectOptional());
		return suite;
	}

	private static Feature<?>[] features() {
		return new Feature<?>[] {
				// since 'OptionalTransformingCollection' passes all calls along,
				// the features are determined by the backing data structure (which is an 'ArrayList')
				CollectionSize.ANY,
				// exclude 'CollectionFeature.ALLOWS_NULL_VALUES' because nulls are handled in a special way TODO
				CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
				CollectionFeature.KNOWN_ORDER,
				CollectionFeature.SUPPORTS_ADD,
				CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
				CollectionFeature.SUPPORTS_REMOVE,
		};
	}

	/**
	 * Creates a test for a collection which us backed by a collection of {@link Optional Optional&lt;String&gt;}.
	 *
	 * @return the test case
	 */
	private static Test testForObjectOptional() {
		return CollectionTestSuiteBuilder
				.using(new ObjectOptionalTestGenerator(null))
				.named("backed by supertype")
				.withFeatures(features())
				.createTestSuite();
	}

	private static class ObjectOptionalTestGenerator implements TestCollectionGenerator<String> {

		private final String defaultValue;

		public ObjectOptionalTestGenerator(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		@Override
		public SampleElements<String> samples() {
			return new SampleElements<String>(defaultValue, "A", "B", "C", "D");
		}

		@Override
		public Collection<String> create(Object... elements) {
			Collection<Optional<String>> optionalStrings = createCollectionOfOptionalStrings(elements);
			return new OptionalTransformingCollection<>(optionalStrings, String.class, defaultValue);
		}

		private Collection<Optional<String>> createCollectionOfOptionalStrings(Object... elements) {
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
