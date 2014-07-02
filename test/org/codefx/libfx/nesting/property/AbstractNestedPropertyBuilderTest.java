package org.codefx.libfx.nesting.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import javafx.beans.property.Property;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests for nested property builders.
 *
 * @param <N>
 *            the nesting hierarchy's innermost type of {@link Property}
 * @param <P>
 *            the type of {@link Property} which will be built
 */
public abstract class AbstractNestedPropertyBuilderTest<N extends Property<?>, P extends NestedProperty<?>> {

	// #region TESTED INSTANCES

	/**
	 * The tested builder.
	 */
	private AbstractNestedPropertyBuilder<N, P> builder;

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
	 * Tests whether calling {@link AbstractNestedPropertyBuilder#setBean(Object)} sets the bean for the created nested
	 * property.
	 */
	@Test
	public void testSetBean() {
		// set a bean on the builder and let it build the property
		Object bean = "Mr. Bean";
		builder.setBean(bean);
		P nestedProperty = builder.build();

		assertEquals(bean, nestedProperty.getBean());
	}

	/**
	 * Tests whether calling {@link AbstractNestedPropertyBuilder#setBean(Object)} with null causes a
	 * {@link NullPointerException}.
	 */
	@Test(expected = NullPointerException.class)
	public void testSetBeanToNull() {
		builder.setBean(null);
	}

	/**
	 * Tests whether calling {@link AbstractNestedPropertyBuilder#setName(String)} sets the name for the created nested
	 * property.
	 */
	@Test
	public void testSetName() {
		// set a name on the builder and let it build the property
		String name = "The Name";
		builder.setName(name);
		P nestedProperty = builder.build();

		assertEquals(name, nestedProperty.getName());
	}

	/**
	 * Tests whether calling {@link AbstractNestedPropertyBuilder#setName(String)} with null causes a
	 * {@link NullPointerException}.
	 */
	@Test(expected = NullPointerException.class)
	public void testSetNameToNull() {
		builder.setName(null);
	}

	/**
	 * Tests whether repeatedly calling {@link AbstractNestedPropertyBuilder#build()} returns different instances of
	 * nested properties.
	 */
	@Test
	public void testBuildCreatesNewInstances() {
		P firstNestedProperty = builder.build();
		P secondNestedProperty = builder.build();

		assertNotSame(firstNestedProperty, secondNestedProperty);
	}

	//#end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Creates the tested builder. Each call must return a new instance
	 *
	 * @return an {@link AbstractNestedPropertyBuilder}
	 */
	protected abstract AbstractNestedPropertyBuilder<N, P> createBuilder();

	//#end ABSTRACT METHODS

}
