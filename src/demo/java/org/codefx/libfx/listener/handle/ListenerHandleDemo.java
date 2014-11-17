package org.codefx.libfx.listener.handle;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

/**
 * Demonstrates how to create and use {@link ListenerHandle}s.
 */
@SuppressWarnings("static-method")
public class ListenerHandleDemo {

	// #region CONSTRUCTION & MAIN

	/**
	 * Creates a new demo.
	 */
	private ListenerHandleDemo() {
		// nothing to do
	}

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		ListenerHandleDemo demo = new ListenerHandleDemo();

		demo.createCommonListenerHandle();
		demo.createCustomListenerHandle();
	}

	// #end CONSTRUCTION & MAIN

	// #region DEMOS

	// construction

	/**
	 * Demonstrates how to simply create a handle for a given observable and listener.
	 */
	private void createCommonListenerHandle() {
		Property<String> property = new SimpleStringProperty();
		ChangeListener<String> listener = (obs, oldValue, newValue) -> { /* do nothing for this demo */};

		// create the handle; this one is initially attached, i.e. the listener is added to the property
		ListenerHandle handle = ListenerHandles.createAttached(property, listener);
		// the handle can be used to easily detach and reattach the listener
		handle.detach();
		handle.attach();

		// create a detached handle where the listener was not yet added to the property
		handle = ListenerHandles.createDetached(property, listener);
		// this one needs to be attached before the listener is executed on changes
		handle.attach();
	}

	/**
	 * Demonstrates how a listener handle can be created for custom observable implementations with
	 * {@link ListenerHandleBuilder}.
	 */
	private void createCustomListenerHandle() {
		MyCustomObservable customObservable = new MyCustomObservable();
		MyCustomListener customListener = new MyCustomListener();

		// use 'ListenerHandles' to get a 'ListenerHandleBuilder' which can be used to create a handle for this
		// observable and listener
		ListenerHandle handleForCustomClasses = ListenerHandles
				.createFor(customObservable, customListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.build();
		handleForCustomClasses.attach();
	}

	// #end DEMOS

	// #region NESTED CLASSES

	/**
	 * Represents a custom observable instance. Note that it is not necessary to implement {@link Observable} (or any
	 * other interface) in order to use this class with the {@link ListenerHandleBuilder}.
	 */
	private static class MyCustomObservable {

		@SuppressWarnings({ "javadoc", "unused" })
		public void addListener(MyCustomListener listener) {
			// do nothing - just for demo
		}

		@SuppressWarnings({ "javadoc", "unused" })
		public void removeListener(MyCustomListener listener) {
			// do nothing - just for demo
		}

	}

	/**
	 * Represents a listener for a custom observable instance. Note that it is not necessary to implement
	 * {@link ChangeListener} (or any other interface) in order to use this class with the {@link ListenerHandleBuilder}
	 * .
	 */
	private static class MyCustomListener {
		// has no members - just for demo
	}

	// #end NESTED CLASSES

}
