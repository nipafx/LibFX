package org.codefx.libfx.nesting.property;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A {@link BooleanProperty} which also implements {@link NestedProperty}.
 */
public class NestedBooleanProperty extends SimpleBooleanProperty implements NestedProperty<Boolean> {

	private final NestedPropertyInternals<Boolean> internals;

	// #begin CONSTUCTION

	/**
	 * Creates a new property.
	 *
	 * @param nesting
	 *            the nesting this property is based on
	 * @param innerObservableMissingBehavior
	 *            defines the behavior for the case that the inner observable is missing
	 * @param bean
	 *            the bean which owns this property; can be null
	 * @param name
	 *            this property's name; can be null
	 */
	NestedBooleanProperty(
			Nesting<? extends Property<Boolean>> nesting,
			InnerObservableMissingBehavior<Boolean> innerObservableMissingBehavior,
			Object bean,
			String name) {

		super(bean, name);
		assert nesting != null : "The argument 'nesting' must not be null.";
		assert innerObservableMissingBehavior != null : "The argument 'innerObservableMissingBehavior' must not be null.";

		this.internals = new NestedPropertyInternals<>(
				this, nesting, innerObservableMissingBehavior, this::setValueSuper);
		internals.initializeBinding();
	}

	//#end CONSTUCTION

	// #begin OVERRIDE SET(VALUE)

	@Override
	public void set(boolean newValue) {
		internals.setCheckingMissingInnerObservable(newValue);
	}

	@Override
	public void setValue(Boolean newValue) {
		internals.setCheckingMissingInnerObservable(newValue);
	}

	private void setValueSuper(Boolean newValue) {
		if (newValue == null)
			super.set(false);
		else
			super.set(newValue.booleanValue());
	}

	// #end OVERRIDE SET(VALUE)

	// #begin IMPLEMENTATION OF 'NestedProperty'

	@Override
	public ReadOnlyBooleanProperty innerObservablePresentProperty() {
		return internals.innerObservablePresentProperty();
	}

	@Override
	public boolean isInnerObservablePresent() {
		return internals.innerObservablePresentProperty().get();
	}

	//#end IMPLEMENTATION OF 'NestedProperty'

}
