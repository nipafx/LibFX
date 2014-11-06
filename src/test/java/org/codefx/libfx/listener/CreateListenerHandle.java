package org.codefx.libfx.listener;

/**
 * Indicates how to create the {@link ListenerHandle}. If the handle is created by a builder, the corresponding method
 * should be called (in order to test it) instead of attaching/detaching the listener after its creation.
 */
public enum CreateListenerHandle {

	/**
	 * The listener must be initially attached.
	 */
	ATTACHED,

	/**
	 * The listener must be initially detached.
	 */
	DETACHED;
}
