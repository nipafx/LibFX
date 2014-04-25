package org.codefx.nesting.types;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Most nesting tests use a simple nesting with an outer and an inner type; this is the outer type.
 */
public class OuterType {

// #region PROPERTIES

	/**
	 * First instance of the inner type.
	 */
	private final ObjectProperty<InnerType> firstInner;

	/**
	 * Second instance of the inner type.
	 */
	private final ObjectProperty<InnerType> secondInner;

// #end PROPERTIES

// #region CONSTRUCTOR

	/**
	 * Creates a new outer type.
	 */
	public OuterType() {
		this.firstInner = new SimpleObjectProperty<InnerType>(this, "firstInner", new InnerType());
		this.secondInner = new SimpleObjectProperty<InnerType>(this, "secondInner", new InnerType());
	}

// #end CONSTRUCTOR

// #region PROPERTY ACCESS

	/**
	 * First instance of the inner type.
	 *
	 * @return the firstInner as a property
	 */
	public ObjectProperty<InnerType> firstInnerProperty() {
		return firstInner;
	}

	/**
	 * First instance of the inner type.
	 *
	 * @return the firstInner
	 */
	public InnerType getFirstInner() {
		return firstInnerProperty().get();
	}

	/**
	 * First instance of the inner type.
	 *
	 * @param firstInner
	 *            the firstInner to set
	 */
	public void setFirstInner(InnerType firstInner) {
		firstInnerProperty().set(firstInner);
	}

	/**
	 * Second instance of the inner type.
	 *
	 * @return the secondInner as a property
	 */
	public ObjectProperty<InnerType> secondInnerProperty() {
		return secondInner;
	}

	/**
	 * Second instance of the inner type.
	 *
	 * @return the secondInner
	 */
	public InnerType getSecondInner() {
		return secondInnerProperty().get();
	}

	/**
	 * Second instance of the inner type.
	 *
	 * @param secondInner
	 *            the secondInner to set
	 */
	public void setSecondInner(InnerType secondInner) {
		secondInnerProperty().set(secondInner);
	}

// #end PROPERTY ACCESS

}
