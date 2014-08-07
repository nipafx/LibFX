package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nested;
import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.NestingObserver;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * Contains an {@link InvalidationListener} which is connected to a {@link Nesting}. Simply put, the listener is always
 * added to the nesting's inner observable (more precisely, it is added to the {@link Observable} instance contained in
 * the optional value held by the nesting's {@link Nesting#innerObservableProperty() innerObservable} property).
 * <p>
 * <h2>Inner Observable's Value is Invalidated</h2> The listener is added to the nesting's inner observable. So when
 * that observable's value is invalidated, the listener is called as usual.
 * <p>
 * <h2>Inner Observable Is Replaced</h2> When the nesting's inner observable is replaced by another, the listener is
 * removed from the old and added to the new observable. If one of them is missing, the affected removal or add is not
 * performed, which means the listener might not be added to any observable.
 * <p>
 * Note that if the observable is replaced, <b>the listener is not called</b>! If this is the desired behavior, a
 * listener has to be added to a {@link NestedProperty}.
 */
public class NestedInvalidationListener implements Nested {

	// #region PROPERTIES

	/**
	 * The property indicating whether the nesting's inner observable is currently present, i.e. not null.
	 */
	private final BooleanProperty innerObservablePresent;

	//#end PROPERTIES

	// #region CONSTUCTION

	/**
	 * Creates a new {@link NestedInvalidationListener} which adds the specified listener to the specified nesting's
	 * inner observable.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener is added
	 * @param listener
	 *            the {@link InvalidationListener} which is added to the nesting's
	 *            {@link Nesting#innerObservableProperty() innerObservable}
	 */
	NestedInvalidationListener(Nesting<? extends Observable> nesting, InvalidationListener listener) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");

		this.innerObservablePresent = new SimpleBooleanProperty(this, "innerObservablePresent");

		NestingObserver
				.forNesting(nesting)
				.withOldInnerObservable(oldInnerObservable -> oldInnerObservable.removeListener(listener))
				.withNewInnerObservable(newInnerObservable -> newInnerObservable.addListener(listener))
				.whenInnerObservableChanges(
						(Boolean any, Boolean newInnerObservablePresent)
						-> innerObservablePresent.set(newInnerObservablePresent))
				.observe();
	}

	//#end CONSTUCTION

	// #region IMPLEMENTATION OF 'Nested'

	@Override
	public ReadOnlyBooleanProperty innerObservablePresentProperty() {
		return innerObservablePresent;
	}

	@Override
	public boolean isInnerObservablePresent() {
		return innerObservablePresent.get();
	}

	//#end IMPLEMENTATION OF 'NestedProperty'

}
