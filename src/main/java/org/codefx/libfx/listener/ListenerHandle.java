package org.codefx.libfx.listener;

/**
 * A listener handle can be used to {@link #attach() attach} and {@link #detach() detach} a listener to/from some
 * observable instance. Using the handler the calling code must not manage references to both the observed instance and
 * the listener, which can improve readability.
 * <p>
 * A handle is created and returned by methods which connect a listener with an observable instance. This usually means
 * that the listener is actually added to the observable but it is also possible to simply return a handler and wait for
 * the call to {@code attach()} before adding the listener. It is up to such methods to specify this behavior.
 * <p>
 * Unless otherwise noted it is not safe to share a handle between different threads. The behavior is undefined if
 * parallel calls are made to {@code attach()} and/or {@code detach()}.
 */
public interface ListenerHandle {

	/**
	 * Adds the listener to the observable. Calling this method when the listener is already added is a no-op and will
	 * not result in the listener being called more than once.
	 */
	void attach();

	/**
	 * Removes the listener from the observable. Calling this method when the listener is not added is a no-op.
	 */
	void detach();

}
