package org.codefx.libfx.listener.handle;

/**
 * A listener handle can be used to {@link #attach() attach} and {@link #detach() detach} a listener to/from some
 * observable instance.
 * <p>
 * Using the handler the calling code does not have to manage references to both the observed instance and the listener,
 * which can improve readability.
 * <p>
 * A handle is created and returned by methods which connect a listener with an observable instance. This usually means
 * that the listener is actually added to the observable but it is also possible to simply return a handler and wait for
 * the call to {@code attach()} before adding the listener. It is up to such methods to specify this behavior.
 * <p>
 * Unless otherwise noted it is not safe to share a handle between different threads. The behavior is undefined if
 * parallel calls are made to {@code attach()} and/or {@code detach()}.
 */
public interface ListenerHandle extends ListenerAttachHandle, ListenerDetachHandle {

	// no additional methods defined

}
