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

	// #region SETUP

	/**
	 * Creates a new builder before each test.
	 */
	@Before
	public void setUp() {
		builder = createBuilder();
	}

	/**
	 * Creates the tested builder. Each call must return a new instance
	 *
	 * @return a {@link NestedInvalidationListenerBuilder}
	 */
	protected abstract NestedInvalidationListenerBuilder createBuilder();

	// #end SETUP

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
		NestedInvalidationListenerHandle listener = builder
				.withListener(observable -> {/* don't do anything */})
				.buildAttached();

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
		buildable.buildAttached();

		// second build must fail
		buildable.buildAttached();
	}

	//#end TESTS

}
