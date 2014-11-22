package org.codefx.libfx.listener.handle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ArrayChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

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
	 * @see ListenerHandleBuilder
	 */
	public static <O, L> ListenerHandleBuilder<O, L> createFor(O observable, L listener) {
		return ListenerHandleBuilder.from(observable, listener);
	}

	// Observable + InvalidationListener

	/**
	 * Adds the specified listener to the specified observable and returns a handle for the combination.
	 *
	 * @param observable
	 *            the {@link Observable} to which the {@code invalidationListener} will be added
	 * @param invalidationListener
	 *            the {@link InvalidationListener} which will be added to the {@code observable}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static ListenerHandle createAttached(Observable observable, InvalidationListener invalidationListener) {
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
				.buildDetached();
	}

	// ObservableValue + ChangeListener

	/**
	 * Adds the specified listener to the specified observable value and returns a handle for the combination.
	 *
	 * @param <T>
	 *            the type of the value wrapped by the observable
	 * @param observableValue
	 *            the {@link ObservableValue} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ChangeListener} which will be added to the {@code observableValue}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static <T> ListenerHandle createAttached(
			ObservableValue<T> observableValue, ChangeListener<? super T> changeListener) {

		ListenerHandle handle = createDetached(observableValue, changeListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable value and listener. The listener is not yet attached!
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
				.buildDetached();
	}

	// ObservableArray + ArrayChangeListener

	/**
	 * Adds the specified listener to the specified observable array and returns a handle for the combination.
	 *
	 * @param <T>
	 *            the type of the array wrapped by the observable
	 * @param observableArray
	 *            the {@link ObservableArray} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ArrayChangeListener} which will be added to the {@code observableArray}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static <T extends ObservableArray<T>> ListenerHandle createAttached(
			ObservableArray<T> observableArray, ArrayChangeListener<T> changeListener) {

		ListenerHandle handle = createDetached(observableArray, changeListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable array and listener. The listener is not yet attached!
	 *
	 * @param <T>
	 *            the type of the array wrapped by the observable
	 * @param observableArray
	 *            the {@link ObservableArray} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ArrayChangeListener} which will be added to the {@code observableArray}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially detached
	 */
	public static <T extends ObservableArray<T>> ListenerHandle createDetached(
			ObservableArray<T> observableArray, ArrayChangeListener<T> changeListener) {

		return ListenerHandleBuilder
				.from(observableArray, changeListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.buildDetached();
	}

	// ObservableList + ListChangeListener

	/**
	 * Adds the specified listener to the specified observable list and returns a handle for the combination.
	 *
	 * @param <E>
	 *            the list element type
	 * @param observableList
	 *            the {@link ObservableList} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ListChangeListener} which will be added to the {@code observableList}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static <E> ListenerHandle createAttached(
			ObservableList<E> observableList, ListChangeListener<? super E> changeListener) {

		ListenerHandle handle = createDetached(observableList, changeListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable list and listener. The listener is not yet attached!
	 *
	 * @param <E>
	 *            the list element type
	 * @param observableList
	 *            the {@link ObservableList} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link ListChangeListener} which will be added to the {@code observableList}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially detached
	 */
	public static <E> ListenerHandle createDetached(
			ObservableList<E> observableList, ListChangeListener<? super E> changeListener) {

		return ListenerHandleBuilder
				.from(observableList, changeListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.buildDetached();
	}

	// ObservableSet + SetChangeListener

	/**
	 * Adds the specified listener to the specified observable set and returns a handle for the combination.
	 *
	 * @param <E>
	 *            the set element type
	 * @param observableSet
	 *            the {@link ObservableSet} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link SetChangeListener} which will be added to the {@code observableSet}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static <E> ListenerHandle createAttached(
			ObservableSet<E> observableSet, SetChangeListener<? super E> changeListener) {

		ListenerHandle handle = createDetached(observableSet, changeListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable set and listener. The listener is not yet attached!
	 *
	 * @param <E>
	 *            the set element type
	 * @param observableSet
	 *            the {@link ObservableSet} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link SetChangeListener} which will be added to the {@code observableSet}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially detached
	 */
	public static <E> ListenerHandle createDetached(
			ObservableSet<E> observableSet, SetChangeListener<? super E> changeListener) {

		return ListenerHandleBuilder
				.from(observableSet, changeListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.buildDetached();
	}

	// ObservableMap + MapChangeListener

	/**
	 * Adds the specified listener to the specified observable map and returns a handle for the combination.
	 *
	 * @param <K>
	 *            the map key element type
	 * @param <V>
	 *            the map value element type
	 * @param observableMap
	 *            the {@link ObservableMap} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link MapChangeListener} which will be added to the {@code observableMap}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially attached
	 */
	public static <K, V> ListenerHandle createAttached(
			ObservableMap<K, V> observableMap, MapChangeListener<? super K, ? super V> changeListener) {

		ListenerHandle handle = createDetached(observableMap, changeListener);
		handle.attach();
		return handle;
	}

	/**
	 * Creates a listener handle for the specified observable map and listener. The listener is not yet attached!
	 *
	 * @param <K>
	 *            the map key element type
	 * @param <V>
	 *            the map value element type
	 * @param observableMap
	 *            the {@link ObservableMap} to which the {@code changeListener} will be added
	 * @param changeListener
	 *            the {@link MapChangeListener} which will be added to the {@code observableMap}
	 * @return a {@link ListenerHandle} for the specified arguments; the listener is initially detached
	 */
	public static <K, V> ListenerHandle createDetached(
			ObservableMap<K, V> observableMap, MapChangeListener<? super K, ? super V> changeListener) {

		return ListenerHandleBuilder
				.from(observableMap, changeListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.buildDetached();
	}

}
