package org.codefx.libfx.nesting.listener;

import static org.junit.Assert.assertNotNull;
import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.Nesting;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of {@link NestedChangeListenerBuilder}.
 */
public abstract class AbstractNestedChangeListenerBuilderTest {

	// #region TESTED INSTANCES

	/**
	 * The tested builder.
	 */
	private NestedChangeListenerBuilder<String, StringProperty> builder;

	//#end TESTED INSTANCES

	/**
	 * Creates a new builder before each test.
	 */
	@Before
	public void setUp() {
		builder = createBuilder();
	}

	// #region TESTS

	/**
	 * Tests whether the builder can be created with a null {@link Nesting}.
	 */
	@Test(expected = NullPointerException.class)
	public void testCreationWithNullNesting() {
		NestedChangeListenerBuilder.forNesting(null);
	}

	/**
	 * Tests whether the builder accepts null as a listener.
	 */
	@Test(expected = NullPointerException.class)
	public void testUsingNullListener() {
		builder.withListener(null);
	}

	/**
	 * Tests whether building creates an instance.
	 */
	@Test
	public void testBuildCreatesInstance() {
		NestedChangeListener<String> listener = builder
				.withListener((observable, oldValue, newValue) -> {/* don't do anything */})
				.build();

		assertNotNull(listener);
	}

	/**
	 * Tests whether building more than once throws an exceptions.
	 */
	@Test(expected = IllegalStateException.class)
	public void testBuildSeveralInstances() {
		NestedChangeListenerBuilder<String, StringProperty>.Buildable buildable =
				builder.withListener((observable, oldValue, newValue) -> {/* don't do anything */});

		// first build must work (see other tests)
		buildable.build();

		// second build must fail
		buildable.build();
	}

	//#end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Creates the tested builder. Each call must return a new instance
	 *
	 * @return a {@link NestedChangeListenerBuilder}
	 */
	protected abstract NestedChangeListenerBuilder<String, StringProperty> createBuilder();

	//#end ABSTRACT METHODS

}
