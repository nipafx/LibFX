package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * A {@link Property} which is based on a {@link Nesting}. Simply put, this property is always bound to the nesting's
 * inner observable (more precisely, it is bound to the {@link Property} instance contained in the optional value held
 * by the nesting's {@link Nesting#innerObservableProperty() innerObservable} property).
 * <p>
 * <h2>Inner Observable's Value Changes</h2> This property is bound to the nesting's inner observable. So when that
 * observable's value changes, so does this property.
 * <p>
 * <h2>Inner Observable Is Replaced</h2> When the nesting's inner observable is replaced by a present observable, this
 * nested property's value changes to the new observable's value. Like all other value changes this one also results in
 * calling invalidation and change listeners.
 * <p>
 * If the new observable is missing, this property stays unbound and keeps its value (and hence no listener is called).
 * <p>
 * <h2>Inner Observable is Missing</h2> It is possible that a nesting's inner observable is missing (see comment on
 * {@link Nesting}). In that case the {@link NestedProperty#innerObservablePresentProperty() innerObservablePresent}
 * property is false and changes made to this property's value can not be propagated to another property.
 * <p>
 * If the inner observable changes back to a present value, everything said above applies. This implies that when the
 * nested property's value changed while the inner observable was missing, these changes are replaced by the new
 * observable's value when one is set. Since this property's change listeners are called, the replaced value can be
 * caught there before it gets lost.
 *
 * @param <T>
 *            the type of the value wrapped by the property
 */
public interface NestedProperty<T> extends Property<T>, ReadOnlyNestedProperty<T> {
	// no additional members defined
}
