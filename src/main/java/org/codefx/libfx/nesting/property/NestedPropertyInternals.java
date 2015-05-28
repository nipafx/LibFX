package org.codefx.libfx.nesting.property;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableGoesMissing;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableMissingOnUpdate;

/**
 * Contains the internal code of nested properties.
 * <p>
 * This class exists to prevent having the same code in all property implementations for the different types. Instead
 * they all delegate their calls to this class. That made it necessary to jump through some hoops and the design is not
 * very elegant.
 *
 * @param <T>
 *            the type of the value wrapped by the property
 */
final class NestedPropertyInternals<T> {

	private final Nesting<? extends Property<T>> nesting;
	private final NestedProperty<T> outerProperty;
	private final Consumer<? super T> setValueDirectly;
	private final InnerObservableMissingBehavior<? extends T> missingBehavior;

	private final BooleanProperty innerObservablePresent;

	/**
	 * Creates a new internals.
	 *
	 * @param outerProperty
	 *            the property whose internals are managed here
	 * @param nesting
	 *            the nesting on which the outer nested property is based
	 * @param missingBehavior
	 *            the behavior for the case that the inner observable is missing
	 * @param setValueDirectly
	 *            called to immediately set the value on the outer property; must set the default value if called with
	 *            null on a property which wraps a primitive type
	 */
	public NestedPropertyInternals(
			NestedProperty<T> outerProperty,
			Nesting<? extends Property<T>> nesting,
			InnerObservableMissingBehavior<? extends T> missingBehavior,
			Consumer<? super T> setValueDirectly) {

		assert nesting != null : "The argument 'nesting' must not be null.";
		assert outerProperty != null : "The argument 'outerProperty' must not be null.";
		assert setValueDirectly != null : "The argument 'setValueDirectly' must not be null.";
		assert missingBehavior != null : "The argument 'missingBehavior' must not be null.";
		assert missingBehavior.whenGoesMissing() != WhenInnerObservableGoesMissing.SET_VALUE_FROM_SUPPLIER
				|| missingBehavior.valueForMissing().isPresent() //
		: "When 'missingBehavior' requests 'SET_VALUE_FROM_SUPPLIER', a supplier must be present.";

		this.nesting = nesting;
		this.outerProperty = outerProperty;
		this.setValueDirectly = setValueDirectly;
		this.innerObservablePresent = new SimpleBooleanProperty(outerProperty, "innerObservablePresent");
		this.missingBehavior = missingBehavior;
	}

	/**
	 * Initializes the binding of the nested property to the nesting's inner observable.
	 */
	public void initializeBinding() {
		bindToInnerObservable(nesting.innerObservableProperty().getValue());
		nesting.innerObservableProperty().addListener(
				(obs, oldInnerObservable, newInnerObservable)
				-> moveBindingToNewInnerObservable(oldInnerObservable, newInnerObservable));
	}

	// #begin BIND TO INNER OBSERVABLE

	private void moveBindingToNewInnerObservable(
			Optional<? extends Property<T>> oldInnerObservable, Optional<? extends Property<T>> newInnerObservable) {
		unbindFromInnerObservable(oldInnerObservable);
		bindToInnerObservable(newInnerObservable);
	}

	private void unbindFromInnerObservable(Optional<? extends Property<T>> innerObservable) {
		innerObservable.ifPresent(outerProperty::unbindBidirectional);
	}

	private void bindToInnerObservable(Optional<? extends Property<T>> innerObservable) {
		innerObservablePresent.set(innerObservable.isPresent());
		if (!innerObservable.isPresent())
			handleMissingInnerObservable();
		innerObservable.ifPresent(outerProperty::bindBidirectional);
	}

	private void handleMissingInnerObservable() {
		WhenInnerObservableGoesMissing whenGoesMissing = missingBehavior.whenGoesMissing();
		switch (whenGoesMissing) {
			case KEEP_VALUE:
				return;
			case SET_DEFAULT_VALUE:
				setDefaultValueIgnoringMissingInnerObservable();
				return;
			case SET_VALUE_FROM_SUPPLIER:
				Supplier<? extends T> supplierForMissingValue = missingBehavior.valueForMissing().get();
				setIgnoringMissingInnerObservable(supplierForMissingValue.get());
				return;
			default:
				throw new IllegalArgumentException("Unknown procedere for missing inner observable: " + whenGoesMissing);
		}
	}

	// #end BIND TO INNER OBSERVABLE

	// #begin SET VALUE

	private void set(T newValue, boolean checkMissingInnerObservable) {
		if (checkMissingInnerObservable)
			maybeThrowExceptionForMissingObservable();
		setValueDirectly.accept(newValue);
	}

	private void maybeThrowExceptionForMissingObservable() {
		boolean innerObservableMissing = !innerObservablePresent.get();
		boolean throwExceptionConfigured =
				missingBehavior.onUpdate() == WhenInnerObservableMissingOnUpdate.THROW_EXCEPTION;
		if (innerObservableMissing && throwExceptionConfigured)
			throw new IllegalStateException("The inner observable is missing so no value can be set.");
	}

	/**
	 * Sets the specified value on the nested property specified during construction.
	 * <p>
	 * If the inner observable is missing, the behavior specified during construction is executed.
	 *
	 * @param newValue
	 *            the new value to set
	 */
	public void setCheckingMissingInnerObservable(T newValue) {
		set(newValue, true);
	}

	private void setIgnoringMissingInnerObservable(T newValue) {
		set(newValue, false);
	}

	private void setDefaultValueIgnoringMissingInnerObservable() {
		set(null, false);
	}

	// #end SET VALUE

	/**
	 * @return whether the nesting's inner observable is present as a property
	 */
	public final ReadOnlyBooleanProperty innerObservablePresentProperty() {
		return innerObservablePresent;
	}

}
