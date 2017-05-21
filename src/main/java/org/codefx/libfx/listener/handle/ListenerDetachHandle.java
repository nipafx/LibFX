package org.codefx.libfx.listener.handle;

/**
 * A listener handle can be used to {@link #detach() detach} a listener from some observable instance.
 *
 * @see ListenerHandle
 */
@FunctionalInterface
public interface ListenerDetachHandle {

	/**
	 * Removes the listener from the observable. Calling this method when the listener is not added is a no-op.
	 */
	void detach();

}
