package org.codefx.libfx.control.properties;

import java.util.function.Consumer;

import javafx.collections.ObservableMap;

import org.codefx.libfx.listener.CreateListenerHandle;

/**
 * Tests {@link CastingControlPropertyListenerHandle}.
 */
public class CastingControlPropertyListenerHandleTest extends AbstractControlPropertyListenerHandleTest {

	@Override
	protected <T> ControlPropertyListenerHandle createListener(
			ObservableMap<Object, Object> properties, Object key,
			Class<T> valueType, Consumer<? super T> valueProcessor,
			CreateListenerHandle attachedOrDetached) {

		ControlPropertyListenerHandle handle =
				new CastingControlPropertyListenerHandle<T>(properties, key, valueProcessor);
		if (attachedOrDetached == CreateListenerHandle.ATTACHED)
			handle.attach();

		return handle;
	}

}
