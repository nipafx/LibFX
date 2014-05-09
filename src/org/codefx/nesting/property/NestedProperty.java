package org.codefx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.nesting.Nested;
import org.codefx.nesting.Nesting;

/**
 * A property which is based on a {@link Nesting}.
 * <p>
 * <h2>Inner Observable's Value Changes</h2> This property is bound to the nesting's inner observable. So when that
 * observable's value changes so does this property.
 * <h2>Inner Observable Changes</h2> When the nesting's inner observable changes to a non-null value, this nested
 * property's value changes to the new observable's value. Like all other value changes this one also results in calling
 * all change listeners. If the new observable is null, the property keeps its value and no change listener is called.
 * <h2>Inner Observable Holds Null</h2> It is possible that a nesting's inner observable holds null (see class comment
 * in {@link Nesting}). In that case the {@link NestedProperty#innerObservableNull() innerObservableNull} property is
 * true and changes made to this property's value can not be propagated to another property. If the inner observable
 * changes back to a non-null value, everything said above applies.
 *
 * @param <T>
 *            the type of the value wrapped by the property
 */
public interface NestedProperty<T> extends Nested, Property<T> {
	// no additional members defined
}
