package org.codefx.libfx.nesting;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Indicates that a class is providing some kind of nested functionality.
 */
public interface Nested {

	/**
	 * Indicates whether the inner observable is currently present.
	 *
	 * @return whether the inner observable is present, i.e. not null (as a property)
	 */
	ReadOnlyBooleanProperty innerObservablePresent();

	/**
	 * Indicates whether the inner observable is currently present.
	 *
	 * @return true if the inner observable is present, i.e. not null
	 */
	boolean isInnerObservablePresent();

}
