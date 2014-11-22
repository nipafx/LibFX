package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.NestingObserver;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * Contains a {@link ChangeListener} which is connected to a {@link Nesting}. Simply put, the listener is always added
 * to the nesting's inner observable (more precisely, it is added to the {@link ObservableValue} instance contained in
 * the optional value held by the nesting's {@link Nesting#innerObservableProperty() innerObservable} property). <h2>
 * Inner Observable's Value Changes</h2> The listener is added to the nesting's inner observable. So when that
 * observable's value changes, the listener is called as usual. <h2>Inner Observable Is Replaced</h2> When the nesting's
 * inner observable is replaced by another, the listener is removed from the old and added to the new observable. If one
 * of them is missing, the affected removal or add is not performed, which means the listener might not be added to any
 * observable.
 * <p>
 * Note that if the observable is replaced, <b>the listener is not called</b>! If this is the desired behavior, a
 * listener has to be added to a {@link NestedProperty}.
 *
 * @param <T>
 *            the type of the value wrapped by the {@link ObservableValue}
 */
public class NestedChangeListenerHandle<T> implements NestedListenerHandle {

	// #region PROPERTIES

	/**
	 * The {@link Nesting} to whose inner observable the {@link #listener} is attached.
	 */
	private final Nesting<? extends ObservableValue<T>> nesting;

	/**
	 * The property indicating whether the nesting's inner observable is currently present, i.e. not null.
	 */
	private final BooleanProperty innerObservablePresent;

	/**
	 * The {@link ChangeListener} which is added to the {@link #nesting}'s inner observable.
	 */
	private final ChangeListener<? super T> listener;

	/**
	 * Indicates whether the {@link #listener} is currently attached to the {@link #nesting}'s inner observable.
	 */
	private boolean attached;

	//#end PROPERTIES

	// #region CONSTUCTION

	/**
	 * Creates a new {@link NestedChangeListenerHandle} which can {@link #attach() attach} the specified listener to the
	 * specified nesting's inner observable.
	 * <p>
	 * The listener is initially detached.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener is added
	 * @param listener
	 *            the {@link ChangeListener} which is added to the nesting's {@link Nesting#innerObservableProperty()
	 *            innerObservable}
	 */
	NestedChangeListenerHandle(Nesting<? extends ObservableValue<T>> nesting, ChangeListener<? super T> listener) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");

		this.nesting = nesting;
		this.innerObservablePresent = new SimpleBooleanProperty(this, "innerObservablePresent");
		this.listener = listener;

		NestingObserver
				.forNesting(nesting)
				.withOldInnerObservable(this::remove)
				.withNewInnerObservable(this::addIfAttached)
				.whenInnerObservableChanges(
						(any, newInnerObservablePresent) -> innerObservablePresent.set(newInnerObservablePresent))
				.observe();
	}

	//#end CONSTUCTION

	// #region ADD & REMOVE

	/**
	 * Adds the {@link #listener} to the specified observable, when indicated by {@link #attached}.
	 *
	 * @param observable
	 *            the {@link ObservableValue} to which the listener will be added
	 */
	private void addIfAttached(ObservableValue<T> observable) {
		if (attached)
			observable.addListener(listener);
	}

	/**
	 * Removes the {@link #listener} from the specified observable.
	 * 
	 * @param observable
	 *            the {@link ObservableValue} from which the listener will be removed.
	 */
	private void remove(ObservableValue<T> observable) {
		observable.removeListener(listener);
	}

	// #end ADD & REMOVE

	// #region IMPLEMENTATION OF 'NestedListenerHandle'

	@Override
	public void attach() {
		if (!attached) {
			attached = true;
			nesting.innerObservableProperty().getValue().ifPresent(this::addIfAttached);
		}
	}

	@Override
	public void detach() {
		if (attached) {
			attached = false;
			nesting.innerObservableProperty().getValue().ifPresent(this::remove);
		}
	}

	@Override
	public ReadOnlyBooleanProperty innerObservablePresentProperty() {
		return innerObservablePresent;
	}

	@Override
	public boolean isInnerObservablePresent() {
		return innerObservablePresent.get();
	}

	//#end IMPLEMENTATION OF 'NestedListenerHandle'

}
