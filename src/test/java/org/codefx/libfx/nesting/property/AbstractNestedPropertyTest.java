package org.codefx.libfx.nesting.property;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingValue;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting.createWithInnerObservable;
import static org.codefx.tarkastus.AssertFX.assertDefault;
import static org.codefx.tarkastus.AssertFX.assertSameOrEqual;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.function.Supplier;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableGoesMissing;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableMissingOnUpdate;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of nested properties. By implementing the few abstract methods subclasses can run all
 * tests which apply to all nested property implementations.
 *
 * @param <S>
 *            the type of the instances contained in the nested property; e.g. {@link Integer} for
 *            {@link NestedIntegerProperty}
 * @param <T>
 *            the type wrapped by the nested property; e.g. {@link Number} for {@link NestedIntegerProperty}
 * @param <P>
 *            the type of property wrapped by the nesting
 */
@SuppressWarnings("javadoc")
public abstract class AbstractNestedPropertyTest<S extends T, T, P extends Property<T>> {

	// #begin INSTANCES USED FOR TESTING

	/**
	 * The nesting on which the tested property is based.
	 */
	private EditableNesting<P> nesting;

	/**
	 * The tested property.
	 */
	private NestedProperty<T> property;

	//#end INSTANCES USED FOR TESTING

	/**
	 * Creates a new instance of {@link #nesting} and {@link #property}.
	 */
	@Before
	public void setUp() {
		P innerObservable = createNewObservableWithSomeValue();
		nesting = createWithInnerObservable(innerObservable);
		property = createNestedPropertyFromNesting(nesting, MissingBehavior.defaults());
	}

	// #begin TESTS

	@Test
	public void innerObservablePresentProperty_getBean_returnsNestedProperty() {
		assertSame(property, property.innerObservablePresentProperty().getBean());
	}

	@Test
	public void getValue_afterConstruction_returnsInnerObservablesValue() {
		// create a nesting with a non-default value for this
		T initialValue = createNewValue();
		nesting = createWithInnerObservable(createNewObservableWithValue(initialValue));

		property = createNestedPropertyFromNesting(nesting, MissingBehavior.defaults());

		assertSameOrEqual(initialValue, nesting.getInnerObservable().get().getValue(), wrapsPrimitive());
		assertSameOrEqual(initialValue, property.getValue(), wrapsPrimitive());
		assertTrue(property.isInnerObservablePresent());
	}

	@Test
	public void setNestingValue_nestedPropertyHoldsSameValue() {
		T newValue = createNewValue();
		setNestingValue(nesting, newValue);

		assertSameOrEqual(newValue, property.getValue(), wrapsPrimitive());
	}

	@Test
	public void setNestingValueToNull_nestedPropertyHoldsNull() {
		setNestingValue(nesting, null);

		assertDefault(property.getValue());
	}

	@Test
	public void setInnerObservable_nestedPropertyHoldsNewObservablesValue() {
		T newValue = createNewValue();
		P newObservable = createNewObservableWithValue(newValue);
		setNestingObservable(nesting, newObservable);

		assertSameOrEqual(newValue, property.getValue(), wrapsPrimitive());
		assertTrue(property.isInnerObservablePresent());
	}

	// inner observable goes missing

	@Test
	public void setInnerObservableToNull_defaultBehavior_propertyKeepsOldValue() {
		T oldValue = property.getValue();
		setNestingObservable(nesting, null);

		assertSameOrEqual(oldValue, property.getValue(), wrapsPrimitive());
		assertFalse(property.isInnerObservablePresent());
	}

	@Test
	public void setInnerObservableToNull_keepValue_propertyKeepsOldValue() {
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.whenGoesMissing(WhenInnerObservableGoesMissing.KEEP_VALUE);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);

		T oldValue = property.getValue();
		setNestingObservable(nesting, null);

		assertSameOrEqual(oldValue, property.getValue(), wrapsPrimitive());
		assertFalse(property.isInnerObservablePresent());
	}

	@Test
	public void setInnerObservableToNull_setDefaultValue_propertyHoldsDefaultValue() {
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.whenGoesMissing(WhenInnerObservableGoesMissing.SET_DEFAULT_VALUE);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);

		setNestingObservable(nesting, null);

		assertDefault(property.getValue());
	}

	@Test
	public void setInnerObservableToNull_setValueFromSupplier_propertyHoldsThatValue() {
		S newValue = createNewValue();
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.whenGoesMissing(WhenInnerObservableGoesMissing.SET_VALUE_FROM_SUPPLIER)
				.valueForMissing(() -> newValue);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);

		setNestingObservable(nesting, null);

		assertSameOrEqual(newValue, property.getValue(), wrapsPrimitive());
	}

	@Test
	public void setInnerObservableToNull_setNullFromSupplier_propertyHoldsDefaultValue() {
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.whenGoesMissing(WhenInnerObservableGoesMissing.SET_VALUE_FROM_SUPPLIER)
				.valueForMissing(() -> null);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);

		setNestingObservable(nesting, null);

		assertDefault(property.getValue());
	}

	// update when inner observable missing

	@Test(expected = IllegalStateException.class)
	public void setValueOnInnerObservableMissing_defaulBehavior_throwException() {
		setNestingObservable(nesting, null);

		property.setValue(createNewValue());
	}

	@Test(expected = IllegalStateException.class)
	public void setValueOnInnerObservableMissing_throw_throwException() {
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.onUpdate(WhenInnerObservableMissingOnUpdate.THROW_EXCEPTION);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);
		setNestingObservable(nesting, null);

		property.setValue(createNewValue());
	}

	@Test
	public void setValueOnInnerObservableMissing_acceptUntilNext_nestedPropertyHoldsNewValue() {
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.onUpdate(WhenInnerObservableMissingOnUpdate.ACCEPT_VALUE_UNTIL_NEXT_INNER_OBSERVABLE);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);
		setNestingObservable(nesting, null);

		// set a new value (which can not be written to the nesting's observable as none is present)
		T newValue = createNewValue();
		property.setValue(newValue);

		assertSameOrEqual(newValue, property.getValue(), wrapsPrimitive());
	}

	@Test
	public void newInnerObservableAfterSetValueOnMissingInnerObservable_acceptUntilNext_newInnerObservableKeepsValue() {
		MissingBehavior<S> missingBehavior = MissingBehavior
				.<S> defaults()
				.onUpdate(WhenInnerObservableMissingOnUpdate.ACCEPT_VALUE_UNTIL_NEXT_INNER_OBSERVABLE);
		property = createNestedPropertyFromNesting(nesting, missingBehavior);
		setNestingObservable(nesting, null);
		T newInnerObservablesValue = createNewValue();
		P newObservable = createNewObservableWithValue(newInnerObservablesValue);

		// change the nested property's value (which can not be written to the nesting's observable as none is present);
		property.setValue(createNewValue());
		// due to the contract of 'createNewValue' the nested property has currently another value than the new observable
		assertNotEquals(newObservable.getValue(), property.getValue());

		// set the new observable and assert that it kept its value and the nested property was updated
		setNestingObservable(nesting, newObservable);
		assertSameOrEqual(newInnerObservablesValue, newObservable.getValue(), wrapsPrimitive());
		assertSameOrEqual(newInnerObservablesValue, property.getValue(), wrapsPrimitive());
	}

	// binding to new inner observable

	@Test
	public void setValueOnNewInnerObservable_nestedPropertyHoldsThatValue() {
		P newObservable = createNewObservableWithSomeValue();
		setNestingObservable(nesting, newObservable);

		// change the new observable's value
		T newValue = createNewValue();
		newObservable.setValue(newValue);

		assertSameOrEqual(newValue, property.getValue(), wrapsPrimitive());
	}

	@Test
	public void setValueOnNestedProperty_newInnerObservableHoldsThatValue() {
		P newObservable = createNewObservableWithSomeValue();
		setNestingObservable(nesting, newObservable);

		// change the nested property's value
		T newValue = createNewValue();
		property.setValue(newValue);

		assertSameOrEqual(newValue, newObservable.getValue(), wrapsPrimitive());
	}

	// unbinding from replaced inner observable

	@Test
	public void setValueOnOldInnerObservable_nestedPropertyDoesNotChange() {
		Property<T> oldObservable = getNestingObservable(nesting);
		setNestingObservable(nesting, createNewObservableWithValue(createNewValue()));

		// let the test fail when the nested property changes
		property.addListener((obs, oldValue, newValue) -> fail());

		// change the old observable's value
		oldObservable.setValue(createNewValue());
	}

	@Test
	public void setValueOnNestedProperty_oldInnerObservableDoesNotChange() {
		Property<T> oldObservable = getNestingObservable(nesting);
		setNestingObservable(nesting, createNewObservableWithValue(createNewValue()));

		// let the test fail when the old observable changes
		oldObservable.addListener((obs, oldValue, newValue) -> fail());

		// change the nested property's value
		property.setValue(createNewValue());
	}

	// #end TESTS

	// #begin ABSTRACT METHODS

	/**
	 * Indicates whether the tested nested property wraps primitive values (e.g. ints).
	 *
	 * @return true if the nested properties wraps primitive values
	 */
	protected abstract boolean wrapsPrimitive();

	/**
	 * Creates the property which will be tested from the specified nesting.
	 *
	 * @param nesting
	 *            the nesting from which the nested property is created
	 * @param missingBehavior
	 *            the behavior for the case that the inner observable is missing
	 * @return a new {@link NestedProperty} instance
	 */
	protected abstract NestedProperty<T> createNestedPropertyFromNesting(
			Nesting<P> nesting, InnerObservableMissingBehavior<S> missingBehavior);

	/**
	 * Creates a new value.
	 * <p>
	 * Each call must return an instance which is not equal to any of those returned before and to that contained in the
	 * observable returned by {@link #createNewObservableWithSomeValue()}.
	 *
	 * @return a new instance of type {@code S}
	 */
	protected abstract S createNewValue();

	/**
	 * Creates a new observable which holds the specified value.
	 * <p>
	 * Each call must return a new instance.
	 *
	 * @param value
	 *            the new observable's value
	 * @return a new {@link Property} instance with the specified value
	 */
	protected abstract P createNewObservableWithValue(T value);

	/**
	 * Creates a new observable which holds some arbitrary value.
	 * <p>
	 * Each call must return a new instance.
	 *
	 * @return a new {@link Property} instance with the specified value
	 */
	protected abstract P createNewObservableWithSomeValue();

	// #end ABSTRACT METHODS

	// #begin HELPERS

	/**
	 * @return the nesting on which the tested property is based
	 */
	protected final EditableNesting<P> getNesting() {
		return nesting;
	}

	/**
	 * @return the tested property
	 */
	protected final NestedProperty<T> getProperty() {
		return property;
	}

	/**
	 * @return the {@link #getProperty tested property}'s current value
	 */
	protected final T getPropertyValue() {
		return property.getValue();
	}

	/**
	 * Sets the specified behavior for missing inner observables on the specified builder.
	 *
	 * @param behavior
	 *            the behavior to set on the builder
	 * @param builder
	 *            the mutated builder
	 */
	protected final void setBehaviorOnBuilder(
			InnerObservableMissingBehavior<S> behavior, AbstractNestedPropertyBuilder<S, ?, ?, ?> builder) {
		// on goes missing
		switch (behavior.whenGoesMissing()) {
			case KEEP_VALUE:
				builder.onInnerObservableMissingKeepValue();
				break;
			case SET_DEFAULT_VALUE:
				builder.onInnerObservableMissingSetDefaultValue();
				break;
			case SET_VALUE_FROM_SUPPLIER:
				builder.onInnerObservableMissingComputeValue(behavior.valueForMissing().get());
				break;
			default:
				throw new IllegalArgumentException();
		}

		// on update
		switch (behavior.onUpdate()) {
			case ACCEPT_VALUE_UNTIL_NEXT_INNER_OBSERVABLE:
				builder.onUpdateWhenInnerObservableMissingAcceptValues();
				break;
			case THROW_EXCEPTION:
				builder.onUpdateWhenInnerObservableMissingThrowException();
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	// #end HELPERS

	// #begin NESTED CLASSES

	/**
	 * Mutable implementation of {@link InnerObservableMissingBehavior}.
	 *
	 * @param <T>
	 *            the type contained in the nested property, e.g. {@link Integer} for {@link NestedIntegerProperty}
	 */
	protected static class MissingBehavior<T> implements InnerObservableMissingBehavior<T> {

		private WhenInnerObservableGoesMissing whenGoesMissing;
		private Optional<? extends Supplier<T>> valueForMissing;
		private WhenInnerObservableMissingOnUpdate onUpdate;

		private MissingBehavior() {}

		/**
		 * Creates the default specification for the behavior when the inner observable is missing.
		 * <p>
		 * The "production code" defines default behavior as well and it could be referenced here. Instead the defaults
		 * are explicitly specified (again) to ensure that changing them in some other place does not happen without
		 * breaking some tests.
		 *
		 * @param <T>
		 *            the type contained in the nested property
		 * @return the default behavior
		 */
		public static <T> MissingBehavior<T> defaults() {
			MissingBehavior<T> behavior = new MissingBehavior<>();
			behavior.whenGoesMissing = WhenInnerObservableGoesMissing.KEEP_VALUE;
			behavior.valueForMissing = Optional.empty();
			behavior.onUpdate = WhenInnerObservableMissingOnUpdate.THROW_EXCEPTION;
			return behavior;
		}

		@Override
		public WhenInnerObservableGoesMissing whenGoesMissing() {
			return whenGoesMissing;
		}

		/**
		 * Determines what happens the inner observable goes missing.
		 *
		 * @param whenGoesMissing
		 *            the desired behavior
		 * @return this behavior
		 */
		public MissingBehavior<T> whenGoesMissing(WhenInnerObservableGoesMissing whenGoesMissing) {
			this.whenGoesMissing = whenGoesMissing;
			return this;
		}

		@Override
		public Optional<? extends Supplier<T>> valueForMissing() {
			return valueForMissing;
		}

		/**
		 * Sets a supplier which is called when the inner observable goes missing and a new value should be set.
		 *
		 * @param valueForMissing
		 *            the supplier for the new value
		 * @return this behavior
		 */
		public MissingBehavior<T> valueForMissing(Supplier<T> valueForMissing) {
			this.valueForMissing = Optional.of(valueForMissing);
			return this;
		}

		@Override
		public WhenInnerObservableMissingOnUpdate onUpdate() {
			return onUpdate;
		}

		/**
		 * Determines what happens when the property is updated while the inner observable is missing.
		 *
		 * @param onUpdate
		 *            the desired behavior
		 * @return this behavior
		 */
		public MissingBehavior<T> onUpdate(WhenInnerObservableMissingOnUpdate onUpdate) {
			this.onUpdate = onUpdate;
			return this;
		}

	}

	// #end NESTED CLASSES
}
