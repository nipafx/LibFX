package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.Nested;
import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.NestingObserver;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * Contains a {@link ChangeListener} which is connected to a {@link Nesting}. Simply put, the listener is always added
 * to the nesting's {@link Nesting#innerObservableProperty() innerObservable}.
 * <p>
 * <h2>Inner Observable's Value Changes</h2> The listener is added to the nesting's inner observable. So when that
 * observable's value changes, the listener is called as usual.
 * <p>
 * <h2>Inner Observable Is Replaced</h2> When the nesting's inner observable is replaced by another, the listener is
 * removed from the old and added to the new observable. If one of them is missing, that step is left out.
 * <p>
 * Note that in this case <b>the listener is not called</b>! If this is the desired behavior, a listener has to be added
 * to a {@link NestedProperty}.
 *
 * @param <T>
 *            the type of the value wrapped by the observable value
 */
public class NestedChangeListener<T> implements Nested {

	// #region PROPERTIES

	/**
	 * The property indicating whether the nesting's inner observable is currently present, i.e. not null.
	 */
	private final BooleanProperty innerObservablePresent;

	//#end PROPERTIES

	// #region CONSTUCTION

	/**
	 * Creates a new {@link NestedChangeListener} which adds the specified listener to the specified nesting's inner
	 * observable.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener is added
	 * @param listener
	 *            the {@link ChangeListener} which is added to the nesting's {@link Nesting#innerObservableProperty()
	 *            innerObservable}
	 */
	NestedChangeListener(Nesting<? extends ObservableValue<T>> nesting, ChangeListener<? super T> listener) {
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
