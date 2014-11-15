package org.codefx.libfx.listener;

/**
 * Utility class for functionality surrounding {@link ListenerHandle}s.
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
	public static <O, L> ListenerHandleBuilder<O, L> from(O observable, L listener) {
		return ListenerHandleBuilder.from(observable, listener);
	}

}
