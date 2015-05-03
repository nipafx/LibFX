package org.codefx.libfx.nesting.listener;

import java.util.Objects;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.NestingObserver;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * Contains an {@link InvalidationListener} which is connected to a {@link Nesting}. Simply put, the listener is always
 * added to the nesting's inner observable (more precisely, it is added to the {@link Observable} instance contained in
 * the optional value held by the nesting's {@link Nesting#innerObservableProperty() innerObservable} property). <h2>
 * Inner Observable's Value is Invalidated</h2> The listener is added to the nesting's inner observable. So when that
 * observable's value is invalidated, the listener is called as usual. <h2>Inner Observable Is Replaced</h2> When the
 * nesting's inner observable is replaced by another, the listener is removed from the old and added to the new
 * observable. If one of them is missing, the affected removal or add is not performed, which means the listener might
 * not be added to any observable.
 * <p>
 * Note that if the observable is replaced, <b>the listener is not called</b>! If this is the desired behavior, a
 * listener has to be added to a {@link NestedProperty}.
 */
public class NestedInvalidationListenerHandle implements NestedListenerHandle {

	// #begin PROPERTIES

	/**
	 * The {@link Nesting} to whose inner observable the {@link #listener} is attached.
	 */
	private final Nesting<? extends Observable> nesting;

	/**
	 * The property indicating whether the nesting's inner observable is currently present, i.e. not null.
	 */
	private final BooleanProperty innerObservablePresent;

	/**
	 * The {@link InvalidationListener} which is added to the {@link #nesting}'s inner observable.
	 */
	private final InvalidationListener listener;

	/**
	 * Indicates whether the {@link #listener} is currently attached to the {@link #nesting}'s inner observable.
	 */
	private boolean attached;

	//#end PROPERTIES

	// #begin CONSTUCTION

	/**
	 * Creates a new {@link NestedInvalidationListenerHandle} which adds the specified listener to the specified
	 * nesting's inner observable.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener is added
	 * @param listener
	 *            the {@link InvalidationListener} which is added to the nesting's
	 *            {@link Nesting#innerObservableProperty() innerObservable}
	 */
	NestedInvalidationListenerHandle(Nesting<? extends Observable> nesting, InvalidationListener listener) {
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

	// #begin ADD & REMOVE

	/**
	 * Adds the {@link #listener} to the specified observable, when indicated by {@link #attached}.
	 *
	 * @param observable
	 *            the {@link Observable} to which the listener will be added
	 */
	private void addIfAttached(Observable observable) {
		if (attached)
			observable.addListener(listener);
	}

	/**
	 * Removes the {@link #listener} from the specified observable.
	 *
	 * @param observable
	 *            the {@link Observable} from which the listener will be removed.
	 */
	private void remove(Observable observable) {
		observable.removeListener(listener);
	}

	// #end ADD & REMOVE

	// #begin IMPLEMENTATION OF 'NestedListenerHandle'

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
