package org.codefx.libfx.listener.handle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Factory class for functionality surrounding {@link ListenerHandle}s.
 */
public class ListenerHandles {

	/**
	 * Private constructor so utility class is not instantiated.
	 */
	private ListenerHandles() {
		// nothing to do
	}

	/**
	 * Creates a {@link ListenerHandleBuilder builder} for a generic {@link ListenerHandle}.
	 *
	 * @param <O>
	 *            the type of the observable instance (e.g {@link javafx.beans.value.ObservableValue ObservableValue} or
	 *            {@link javafx.collections.ObservableMap ObservableMap}) to which the listener will be added
	 * @param <L>
	 *            the type of the listener which will be added to the observable
	 * @param observable
	 *            the observable instance to which the {@code listener} will be added
	 * @param listener
	 *            the listener which will be added to the {@code observable}
	 * @return a {@link ListenerHandleBuilder} for a {@code ListenerHandle}.
	 */
	public static <O, L> ListenerHandleBuilder<O, L> buildFor(O observable, L listener) {
		return ListenerHandleBuilder.from(observable, listener);
	}

	// Observable + InvalidationListener

	/**
	 * Ands the specified listener to the specified observable and returns a handle for the combination.
	 *
	 * @param observable
	 *            the {@link Observable} to which the {@code invalidationListener} will be added
	 * @param invalidationListener
	 *            the {@link InvalidationListener} which will be added to the {@code observable}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static ListenerHandle create(Observable observable, InvalidationListener invalidationListener) {
		ListenerHandle handle = createDetached(observable, invalidationListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable and listener. The listener is not yet attached!
	 *
	 * @param observable
	 *            the {@link Observable} to which the {@code invalidationListener} will be added
	 * @param invalidationListener
	 *            the {@link InvalidationListener} which will be added to the {@code observableValue}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially detached
	 */
	public static ListenerHandle createDetached(Observable observable, InvalidationListener invalidationListener) {
		return ListenerHandleBuilder
				.from(observable, invalidationListener)
				.onAttach((obs, listener) -> obs.addListener(listener))
				.onDetach((obs, listener) -> obs.removeListener(listener))
				.build();
	}

	// ObservableValue + ChangeListener

	/**
	 * Ands the specified listener to the specified observable and returns a handle for the combination.
	 *
	 * @param <T>
	 *            the type of the value wrapped by the observable
	 * @param observableValue
	 *            the {@link ObservableValue} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ChangeListener} which will be added to the {@code observableValue}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static <T> ListenerHandle create(
			ObservableValue<T> observableValue, ChangeListener<? super T> changeListener) {

		ListenerHandle handle = createDetached(observableValue, changeListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable and listener. The listener is not yet attached!
	 *
	 * @param <T>
	 *            the type of the value wrapped by the observable
	 * @param observableValue
	 *            the {@link ObservableValue} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ChangeListener} which will be added to the {@code observableValue}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially detached
	 */
	public static <T> ListenerHandle createDetached(
			ObservableValue<T> observableValue, ChangeListener<? super T> changeListener) {

		return ListenerHandleBuilder
				.from(observableValue, changeListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.build();
	}

}
