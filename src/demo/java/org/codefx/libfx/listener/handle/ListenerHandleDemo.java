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
		demo.attachAndDetach();
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
		ListenerHandles
				.createFor(customObservable, customListener)
				.onAttach((observable, listener) -> observable.addListener(listener))
				.onDetach((observable, listener) -> observable.removeListener(listener))
				.buildAttached();
	}

	// attach & detach

	/**
	 * Demonstrates how to add and remove a listener with a {@link ListenerHandle} and compares this to the normal
	 * approach.
	 */
	private void attachAndDetach() {
		Property<String> observedProperty = new SimpleStringProperty("initial value");

		// usually a listener is directly added to the property;
		// but if the listener has to be removed later, the reference needs to be stored explicitly
		ChangeListener<Object> changePrintingListener = (obs, oldValue, newValue) ->
				System.out.println("[LISTENER] Value changed from \"" + oldValue + "\" to \"" + newValue + "\".");
		observedProperty.addListener(changePrintingListener);

		// this is the alternative with a 'ListenerHandle'
		ListenerHandle newValuePrinter = ListenerHandles.createAttached(observedProperty,
				(obs, oldValue, newValue) -> System.out.println("[HANDLE] New value: \"" + newValue + "\""));

		// now lets change the value to see how it works
		observedProperty.setValue("new value");
		observedProperty.setValue("even newer value");

		// removing a listener needs references to both the observable and the listener;
		// depending on the situation this might not be feasible
		observedProperty.removeListener(changePrintingListener);
		// with a handle, the listener can be removed without giving the caller the possibility tp interact with
		// the observable or the listener; it is also a little more readable
		newValuePrinter.detach();

		// some unobserved changes...
		observedProperty.setValue("you won't see this on the console");
		observedProperty.setValue("nor this");

		// the same as above goes for adding the listener
		observedProperty.addListener(changePrintingListener);
		newValuePrinter.attach();

		// now some more changes
		observedProperty.setValue("but you will see this");
		observedProperty.setValue("and this");
	}

	// #end DEMOS

	// #region NESTED CLASSES

	/**
	 * Represents a custom observable instance. Note that it is not necessary to implement {@link Observable} (or any
	 * other interface) in order to use this class with the {@link ListenerHandleBuilder}.
	 */
	private static class MyCustomObservable {

		@SuppressWarnings({ "unused" })
		public void addListener(MyCustomListener listener) {
			// do nothing - just for demo
		}

		@SuppressWarnings({ "unused" })
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
