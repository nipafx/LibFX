package org.codefx.libfx.nesting.property;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * An {@link IntegerProperty} which also implements {@link NestedProperty}.
 */
public class NestedIntegerProperty extends SimpleIntegerProperty implements NestedProperty<Number> {

	private final NestedPropertyInternals<Number> internals;

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
	NestedIntegerProperty(
			Nesting<? extends Property<Number>> nesting,
			InnerObservableMissingBehavior<? extends Number> innerObservableMissingBehavior,
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
	public void set(int newValue) {
		internals.setCheckingMissingInnerObservable(newValue);
	}

	@Override
	public void setValue(Number newValue) {
		internals.setCheckingMissingInnerObservable(newValue);
	}

	private void setValueSuper(Number newValue) {
		if (newValue == null)
			super.set(0);
		else
			super.set(newValue.intValue());
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
