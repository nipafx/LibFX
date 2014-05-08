package org.codefx.nesting.testhelper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Most nesting tests use a simple nesting with an outer and an inner value; this is the outer value.
 */
public class OuterValue {

	/**
	 * Instance of the inner value.
	 */
	private final ObjectProperty<InnerValue> innerValue;

	/**
	 * Creates a new outer value.
	 *
	 * @param inner
	 *            the inner type
	 */
	private OuterValue(ObjectProperty<InnerValue> inner) {
		this.innerValue = inner;
	}

	/**
	 * @return a new outer value with an instantiated inner type, which in turn has instantiated observables
	 */
	public static OuterValue createWithInnerType() {
		return new OuterValue(new SimpleObjectProperty<InnerValue>(InnerValue.createWithObservables()));
	}

	/**
	 * Instance of the inner value.
	 *
	 * @return the innerValue as a property
	 */
	public ObjectProperty<InnerValue> innerValueProperty() {
		return innerValue;
	}

	/**
	 * Instance of the inner value.
	 *
	 * @return the innerValue
	 */
	public InnerValue getInnerValue() {
		return innerValueProperty().get();
	}

	/**
	 * Instance of the inner value.
	 *
	 * @param innerValue
	 *            the innerValue to set
	 */
	public void setInnerValue(InnerValue innerValue) {
		innerValueProperty().set(innerValue);
	}

}
