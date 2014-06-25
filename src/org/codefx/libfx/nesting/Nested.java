package org.codefx.libfx.nesting;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Indicates that a class is providing some kind of nested functionality.
 */
public interface Nested {

	/**
	 * Indicates whether the inner observable is currently null.
	 *
	 * @return whether the inner observable is null as a property
	 */
	ReadOnlyBooleanProperty innerObservableNull();

	/**
	 * Indicates whether the inner observable is currently null.
	 *
	 * @return true if the inner observable is null
	 */
	boolean isInnerObservableNull();

}
