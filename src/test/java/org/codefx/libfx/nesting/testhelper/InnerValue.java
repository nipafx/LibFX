package org.codefx.libfx.nesting.testhelper;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Most nesting tests use a simple nesting with an outer and an inner value; this is the inner value.
 */
public class InnerValue {

// #region PROPERTIES

	/**
	 * An observable.
	 */
	private final Observable observable;

	/**
	 * A property.
	 */
	private final Property<SomeValue> property;

	/**
	 * An integer property.
	 */
	private final IntegerProperty integerProperty;

// #end PROPERTIES

// #region CONSTRUCTOR

	/**
	 * Creates a new inner value with the specified observables.
	 *
	 * @param observable
	 *            the observable
	 * @param property
	 *            the property
	 * @param integerProperty
	 *            the integer property
	 */
	private InnerValue(Observable observable, Property<SomeValue> property, IntegerProperty integerProperty) {
		this.observable = observable;
		this.property = property;
		this.integerProperty = integerProperty;
	}

	/**
	 * @return a new inner value whose observables are all null.
	 */
	public static InnerValue createWithNulls() {
		return new InnerValue(null, null, null);
	}

	/**
	 * @return a new inner value whose observables are all instantiated
	 */
	public static InnerValue createWithObservables() {
		Observable observable = new SimpleObjectProperty<>();
		Property<SomeValue> property = new SimpleObjectProperty<>();
		IntegerProperty integerProperty = new SimpleIntegerProperty(1);

		return new InnerValue(observable, property, integerProperty);
	}

// #end CONSTRUCTOR

// #region PROPERTY ACCESS

	/**
	 * An observable.
	 *
	 * @return the observable
	 */
	public Observable observable() {
		return observable;
	}

	/**
	 * A property.
	 *
	 * @return the property
	 */
	public Property<SomeValue> property() {
		return property;
	}

	/**
	 * An integer property.
	 *
	 * @return the integer as a property
	 */
	public IntegerProperty integerProperty() {
		return integerProperty;
	}

// #end PROPERTY ACCESS

}
