package org.codefx.libfx.control.properties;

import java.util.function.Consumer;

import javafx.collections.ObservableMap;

import org.codefx.libfx.listener.handle.CreateListenerHandle;

/**
 * Tests {@link TypeCheckingControlPropertyListenerHandle}.
 */
public class TypeCheckingControlPropertyListenerHandleTest extends AbstractControlPropertyListenerHandleTest {

	@Override
	protected <T> ControlPropertyListenerHandle createListener(
			ObservableMap<Object, Object> properties, Object key,
			Class<T> valueType, Consumer<? super T> valueProcessor,
			CreateListenerHandle attachedOrDetached) {

		ControlPropertyListenerHandle handle =
				new TypeCheckingControlPropertyListenerHandle<>(properties, key, valueType, valueProcessor);
		if (attachedOrDetached == CreateListenerHandle.ATTACHED)
			handle.attach();

		return handle;
	}

}
