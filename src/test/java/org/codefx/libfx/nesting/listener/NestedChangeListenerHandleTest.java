package org.codefx.libfx.nesting.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.listener.handle.CreateListenerHandle;
import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedChangeListenerHandle}.
 */
public class NestedChangeListenerHandleTest extends AbstractNestedChangeListenerHandleTest {

	@Override
	protected <T> NestedChangeListenerHandle<T> createNestedListenerHandle(
			Nesting<? extends ObservableValue<T>> nesting,
			ChangeListener<T> listener,
			CreateListenerHandle attachedOrDetached) {

		NestedChangeListenerHandle<T> listenerHandle = new NestedChangeListenerHandle<T>(nesting, listener);
		if (attachedOrDetached == CreateListenerHandle.ATTACHED)
			listenerHandle.attach();
		return listenerHandle;
	}

}
