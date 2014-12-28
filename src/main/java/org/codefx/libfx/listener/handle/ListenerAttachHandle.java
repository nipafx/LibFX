package org.codefx.libfx.listener.handle;

/**
 * A listener handle can be used to {@link #attach() attach} a listener to some observable instance.
 *
 * @see ListenerHandle
 */
public interface ListenerAttachHandle {

	/**
	 * Adds the listener to the observable. Calling this method when the listener is already added is a no-op and will
	 * not result in the listener being called more than once.
	 */
	void attach();

}
