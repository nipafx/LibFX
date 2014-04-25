package org.codefx.nesting.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.Nestings;
import org.codefx.nesting.types.InnerType;
import org.codefx.nesting.types.OuterType;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the class {@link NestedObjectProperty}.
 */
public class NestedObjectPropertyTest {

	/**
	 * The outer observable for the tests.
	 */
	ObjectProperty<OuterType> outerProperty;

	/**
	 * Creates a new instance of {@link #outerProperty}.
	 */
	@Before
	public void setUp() {
		outerProperty = new SimpleObjectProperty<>(new OuterType());
	}

	/**
	 * Tests whether a property with a nesting of depth > 1 can be built.
	 */
	@Test
	public void testBuildDeepNesting() {
		Property<Number> nestedProperty = Nestings
				.on(outerProperty)
				.nest(someOuterType -> someOuterType.firstInnerProperty())
				.nest(someInnerType -> someInnerType.integerProperty())
				.buildProperty();

		// the nested property's and the inner property's values must equal
		assertEquals(getFirstInteger(), nestedProperty.getValue());
	}

	/**
	 * Tests whether a property with a nesting of depth > 1 correctly updates the inner property's value when the nested
	 * property's value is edited.
	 */
	@Test
	public void testDeepNestingWhenChangingNestedPropertyValue() {
		Property<Number> nestedProperty = Nestings
				.on(outerProperty)
				.nest(someOuterType -> someOuterType.firstInnerProperty())
				.nest(someInnerType -> someInnerType.integerProperty())
				.buildProperty();

		// edit the nested values and assert that the inner values equal
		for (int newIntegerValue = 0; newIntegerValue < 10; newIntegerValue++) {
			nestedProperty.setValue(newIntegerValue);
			assertEquals(newIntegerValue, nestedProperty.getValue());
			assertEquals(newIntegerValue, getFirstInteger());
		}
	}

	/**
	 * Tests whether a property with a nesting of depth > 1 correctly updates its value when the inner property's value
	 * is edited.
	 */
	@Test
	public void testDeepNestingWhenChangingInnerPropertyValue() {
		Property<Number> nestedProperty = Nestings
				.on(outerProperty)
				.nest(someOuterType -> someOuterType.firstInnerProperty())
				.nest(someInnerType -> someInnerType.integerProperty())
				.buildProperty();

		// edit the inner values and assert that the nested values equal
		for (int newIntegerValue = 0; newIntegerValue < 10; newIntegerValue++) {
			setFirstInteger(newIntegerValue);
			assertEquals(newIntegerValue, getFirstInteger());
			assertEquals(newIntegerValue, nestedProperty.getValue());
		}
	}

	/**
	 * Tests whether a property with a nesting of depth > 1 behaves correctly:
	 * <ul>
	 * <li>it updates when one of the inner instances is replaced by another instance
	 * <li>the replaced inner instance must not change when the nested property's value is changed
	 * </ul>
	 */
	@Test
	public void testDeepNestingWhenChangingInnerInstance() {
		Property<Number> nestedProperty = Nestings
				.on(outerProperty)
				.nest(someOuterType -> someOuterType.firstInnerProperty())
				.nest(someInnerType -> someInnerType.integerProperty())
				.buildProperty();

		// edit the inner values and assert that the nested values equal
		for (int newIntegerValue = 0; newIntegerValue < 10; newIntegerValue++) {
			// assert that the currently bound inner instance is not edited anymore;
			// because it is replaced by other inner instances, this means that its value must not change during this test
			getFirstInner().integerProperty().addListener((observable, oldValue, newValue) -> fail());

			InnerType newInner = new InnerType();
			newInner.setInteger(newIntegerValue);
			setFirstInner(newInner);

			assertEquals(newIntegerValue, getFirstInteger());
			// assert that the nested property holds the correct value
			assertEquals(getFirstInteger(), nestedProperty.getValue());

			// assert that the binding "nested bound to inner" works by changing the new inner instance's integer
			for (int factor = 2; factor < 5; factor++) {
				int newValue = factor * newIntegerValue;
				newInner.setInteger(newValue);
				assertEquals(newValue, getFirstInteger());
				assertEquals(newValue, nestedProperty.getValue());
			}

			// assert that the binding "inner bound to nested" works by changing the nested property's value
			for (int factor = 5; factor < 8; factor++) {
				int newValue = factor * newIntegerValue;
				nestedProperty.setValue(newValue);
				assertEquals(newValue, nestedProperty.getValue());
				assertEquals(newValue, getFirstInteger());
			}
		}
	}

	// #region NESTED INSTANCES ACCESS

	/**
	 * @return the current outer type instance
	 */
	private OuterType getOuter() {
		return outerProperty.get();
	}

	/**
	 * @return the current first inner type instance
	 */
	private InnerType getFirstInner() {
		return getOuter().getFirstInner();
	}

	/**
	 * Sets the current first instance of inner type.
	 */
	private void setFirstInner(InnerType inner) {
		getOuter().setFirstInner(inner);
	}

	/**
	 * @return the current second inner type instance
	 */
	private InnerType getSecondInner() {
		return getOuter().getSecondInner();
	}

	/**
	 * Sets the current second instance of inner type.
	 */
	private void setSecondInner(InnerType inner) {
		getOuter().setSecondInner(inner);
	}

	/**
	 * @return the current first inner type instance's integer
	 */
	private int getFirstInteger() {
		return getFirstInner().getInteger();
	}

	/**
	 * Sets the current first inner type instance's integer.
	 */
	private void setFirstInteger(int value) {
		getFirstInner().setInteger(value);
	}

	/**
	 * @return the current second inner type isntance's integer
	 */
	private int getSecondInteger() {
		return getSecondInner().getInteger();
	}

	/**
	 * Sets the current first inner type instance's integer.
	 */
	private void setSecondInteger(int value) {
		getSecondInner().setInteger(value);
	}

	//#end NESTED INSTANCES ACCESS

}
