package org.codefx.libfx.nesting.listener;

import static org.junit.Assert.assertNotNull;

import org.codefx.libfx.nesting.Nesting;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of {@link NestedInvalidationListenerBuilder}.
 */
public abstract class AbstractNestedInvalidationListenerBuilderTest {

	// #region TESTED INSTANCES

	/**
	 * The tested builder.
	 */
	private NestedInvalidationListenerBuilder builder;

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
		NestedInvalidationListenerBuilder.forNesting(null);
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
		NestedInvalidationListener listener = builder
				.withListener(observable -> {/* don't do anything */})
				.build();

		assertNotNull(listener);
	}

	/**
	 * Tests whether building more than once throws an exceptions.
	 */
	@Test(expected = IllegalStateException.class)
	public void testBuildSeveralInstances() {
		NestedInvalidationListenerBuilder.Buildable buildable =
				builder.withListener(observable -> {/* don't do anything */});

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
	 * @return a {@link NestedInvalidationListenerBuilder}
	 */
	protected abstract NestedInvalidationListenerBuilder createBuilder();

	//#end ABSTRACT METHODS

}
