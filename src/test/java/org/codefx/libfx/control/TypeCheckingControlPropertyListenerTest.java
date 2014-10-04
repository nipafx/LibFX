package org.codefx.libfx.control;

import java.util.function.Consumer;

import javafx.collections.ObservableMap;

/**
 * Tests {@link TypeCheckingControlPropertyListener}.
 */
public class TypeCheckingControlPropertyListenerTest extends AbstractControlPropertyListenerTest {

	@Override
	protected <T> ControlPropertyListener createListener(
			ObservableMap<Object, Object> properties, Object key,
			Class<T> valueType, Consumer<T> valueProcessor) {

		return new TypeCheckingControlPropertyListener<T>(properties, key, valueType, valueProcessor);
	}

}
