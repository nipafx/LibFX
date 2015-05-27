package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * <p>
 * A {@link Property} which is based on a {@link Nesting}.
 * <p>
 * Simply put, this property is always bound to the nesting's inner observable (more precisely, it is bound to the
 * {@link Property} instance contained in the optional value held by the nesting's
 * {@link Nesting#innerObservableProperty() innerObservable} property).
 * <h2>Value Changes</h2> This property is bidirectionally bound to the nesting's inner observable. So when that
 * observable's value changes, so does this property's value and vice versa.
 * <h2>Inner Observable Is Replaced</h2> When the nesting's inner observable is replaced by a present observable, this
 * nested property's value changes to the new observable's value. Like all other value changes this one also results in
 * calling invalidation and change listeners.
 * <h2>Missing Inner Observable</h2> It is possible that a nesting's inner observable is missing (see comment on
 * {@link Nesting}). In that case the {@link NestedProperty#innerObservablePresentProperty() innerObservablePresent}
 * property is false. How else the nested property behaves depends on its configuration which was determined when it was
 * build.
 * <h3>When Inner Observable Goes Missing</h3> When the inner observable goes missing, the nested property will either
 * keep its value (this is the default behavior) or change to a value which was determined at build time. This can be
 * done with the {@code onInnerObservableMissing...} methods on the nested property builder (e.g.
 * {@link NestedObjectPropertyBuilder#onInnerObservableMissingSetDefaultValue() onInnerObservableMissingSetDefaultValue}
 * ).
 * <h3>Update While Inner Observable Is Missing</h3> When a value is set on the nested property while the inner
 * observable is missing, it can not be propagated anywhere. For this reason the default behavior is to throw an
 * exception. Alternatively the property can hold new values until a new inner observable is found (with
 * {@link NestedObjectPropertyBuilder#onUpdateWhenInnerObservableMissingAcceptValues()
 * onUpdateWhenInnerObservableMissingAcceptValues}). The property will then be bound to the new observable and hence
 * forget the intermediate value. (Since this property's change listeners are called, the replaced value can be caught
 * there before it gets lost.)
 *
 * @param <T>
 *            the type of the value wrapped by the property
 */
public interface NestedProperty<T> extends Property<T>, ReadOnlyNestedProperty<T> {
	// no additional members defined
}
