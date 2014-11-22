package org.codefx.libfx.nesting.listener;

import org.codefx.libfx.listener.handle.ListenerHandle;
import org.codefx.libfx.nesting.Nested;

/**
 * A {@link ListenerHandle} for a listener added to the inner observable of a {@link org.codefx.libfx.nesting.Nesting
 * Nesting}.
 * 
 * @see Nested
 * @see ListenerHandle
 */
public interface NestedListenerHandle extends Nested, ListenerHandle {
	// no additional methods defined
}
