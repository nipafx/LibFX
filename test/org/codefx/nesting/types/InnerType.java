package org.codefx.nesting.types;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Most nesting tests use a simple nesting with an outer and an inner type; this is the inner type.
 */
public class InnerType {

// #region PROPERTIES

	/**
	 * An integer property.
	 */
	private final IntegerProperty integer;

// #end PROPERTIES

// #region CONSTRUCTOR

	/**
	 * Creates a new inner type.
	 */
	public InnerType() {
		this.integer = new SimpleIntegerProperty(this, "integer", 1);
	}

// #end CONSTRUCTOR

// #region PROPERTY ACCESS

	/**
	 * An integer property.
	 *
	 * @return the integer as a property
	 */
	public IntegerProperty integerProperty() {
		return integer;
	}

	/**
	 * An integer property.
	 *
	 * @return the integer
	 */
	public int getInteger() {
		return integerProperty().get();
	}

	/**
	 * An integer property.
	 *
	 * @param integer
	 *            the integer to set
	 */
	public void setInteger(int integer) {
		integerProperty().set(integer);
	}

// #end PROPERTY ACCESS

}
