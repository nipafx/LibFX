package org.codefx.libfx.nesting.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import javafx.beans.property.Property;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests for nested property builders.
 * <p>
 * Some behavior of the builders is already tested in the property tests (e.g. the behavior when the inner observable is
 * missing). This test cover the rest of the functionality.
 *
 * @param <N>
 *            the nesting hierarchy's inner type of {@link Property}
 * @param <P>
 *            the type of {@link Property} which will be built
 */
@SuppressWarnings("javadoc")
public abstract class AbstractNestedPropertyBuilderTest<N extends Property<?>, P extends NestedProperty<?>> {

	// #begin TESTED INSTANCES

	private AbstractNestedPropertyBuilder<?, N, P, ?> builder;

	//#end TESTED INSTANCES

	@Before
	public void setUp() {
		builder = createBuilder();
	}

	// #begin TESTS

	@Test(expected = NullPointerException.class)
	public void setBean_nullBean_throwsException() {
		builder.setBean(null);
	}

	@Test
	public void setBean_validBean_builtPropertyBelongsToThatBean() {
		// set a bean on the builder and let it build the property
		Object bean = "Mr. Bean";
		builder.setBean(bean);
		P nestedProperty = builder.build();

		assertEquals(bean, nestedProperty.getBean());
	}

	@Test(expected = NullPointerException.class)
	public void setName_nullName_throwsException() {
		builder.setName(null);
	}

	@Test
	public void setName_validName_builrPropertyHasThatName() {
		// set a name on the builder and let it build the property
		String name = "The Name";
		builder.setName(name);
		P nestedProperty = builder.build();

		assertEquals(name, nestedProperty.getName());
	}

	@Test
	public void callBuildRepeatedly_createsNewInstances() {
		P firstNestedProperty = builder.build();
		P secondNestedProperty = builder.build();

		assertNotSame(firstNestedProperty, secondNestedProperty);
	}

	//#end TESTS

	// #begin ABSTRACT METHODS

	/**
	 * Creates the tested builder. Each call must return a new instance
	 *
	 * @return an {@link AbstractNestedPropertyBuilder}
	 */
	protected abstract AbstractNestedPropertyBuilder<?, N, P, ?> createBuilder();

	//#end ABSTRACT METHODS

}
