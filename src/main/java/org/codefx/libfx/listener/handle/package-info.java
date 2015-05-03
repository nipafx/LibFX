/**
 * Provides classes which make it easier to add and remove listeners from observable instances.
 * <p>
 * Using the default JavaFX 8 features, it is necessary to store both the observed instance and the listener if the
 * latter has to be added or removed repeatedly. A {@link org.codefx.libfx.listener.handle.ListenerHandle
 * ListenerHandle} encapsulates those references and the state whether a listener is currently added or not. It provides
 * an {@link org.codefx.libfx.listener.handle.ListenerHandle#attach() attach()} and a
 * {@link org.codefx.libfx.listener.handle.ListenerHandle#detach() detach} method which add or remove the listener.
 * Redundant calls (i.e. attaching when the listener is already added) are no-ops.
 * <p>
 * All features of <b>LibFX</b> which deal with listeners are aware of {@code ListenerHandle}s and respective methods
 * will return them. For observable classes included in the JDK, the factory
 * {@link org.codefx.libfx.listener.handle.ListenerHandles ListenerHandles} provides methods to easily create a handle.
 *
 * @see org.codefx.libfx.listener.handle.ListenerHandle ListenerHandle
 * @see org.codefx.libfx.listener.handle.ListenerHandles ListenerHandles
 */
package org.codefx.libfx.listener.handle;