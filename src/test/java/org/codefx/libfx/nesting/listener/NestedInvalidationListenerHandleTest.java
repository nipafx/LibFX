package org.codefx.libfx.nesting.listener;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import org.codefx.libfx.listener.CreateListenerHandle;
import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedInvalidationListenerHandle}.
 */
public class NestedInvalidationListenerHandleTest extends AbstractNestedInvalidationListenerHandleTest {

	@Override
	protected NestedInvalidationListenerHandle createNestedListenerHandle(
			Nesting<? extends Observable> nesting,
			InvalidationListener listener,
			CreateListenerHandle attachedOrDetached) {

		NestedInvalidationListenerHandle listenerHandle = new NestedInvalidationListenerHandle(nesting, listener);
		if (attachedOrDetached == CreateListenerHandle.ATTACHED)
			listenerHandle.attach();
		return listenerHandle;
	}

}
