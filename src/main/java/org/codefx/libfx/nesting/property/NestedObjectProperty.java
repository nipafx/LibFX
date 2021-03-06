package org.codefx.libfx.nesting.property;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * An {@link ObjectProperty} which also implements {@link NestedProperty}.
 *
 * @param <T>
 *            the type of the value wrapped by this property
 */
public class NestedObjectProperty<T> extends SimpleObjectProperty<T> implements NestedProperty<T> {

	private final NestedPropertyInternals<T> internals;

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
	NestedObjectProperty(
			Nesting<? extends Property<T>> nesting,
			InnerObservableMissingBehavior<? extends T> innerObservableMissingBehavior,
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
	public void set(T newValue) {
		internals.setCheckingMissingInnerObservable(newValue);
	}

	@Override
	public void setValue(T newValue) {
		internals.setCheckingMissingInnerObservable(newValue);
	}

	private void setValueSuper(T newValue) {
		super.set(newValue);
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
