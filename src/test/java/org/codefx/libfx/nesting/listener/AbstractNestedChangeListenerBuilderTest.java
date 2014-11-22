package org.codefx.libfx.nesting.listener;

import static org.junit.Assert.assertNotNull;
import javafx.beans.property.Property;

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
	private NestedChangeListenerBuilder<String, Property<String>> builder;

	//#end TESTED INSTANCES

	// #region SETUP

	/**
	 * Creates a new builder before each test.
	 */
	@Before
	public void setUp() {
		builder = this.<String> createBuilder();
	}

	/**
	 * Creates the tested builder. Each call must return a new instance
	 *
	 * @param <T>
	 *            the value wrapped by the nesting's inner observable, which is also the type observed by the change
	 *            listener
	 * @return a {@link NestedChangeListenerBuilder}
	 */
	protected abstract <T> NestedChangeListenerBuilder<T, Property<T>> createBuilder();

	// #end SETUP

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
		NestedChangeListenerHandle<String> listener = builder
				.withListener((observable, oldValue, newValue) -> {/* don't do anything */})
				.build();

		assertNotNull(listener);
	}

	/**
	 * Tests whether building more than once throws an exceptions.
	 */
	@Test(expected = IllegalStateException.class)
	public void testBuildSeveralInstances() {
		NestedChangeListenerBuilder<String, Property<String>>.Buildable buildable =
				builder.withListener((observable, oldValue, newValue) -> {/* don't do anything */});

		// first build must work (see other tests)
		buildable.build();

		// second build must fail
		buildable.build();
	}

	//#end TESTS

}
