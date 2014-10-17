package org.codefx.libfx.control.properties;

import java.util.function.Consumer;

import org.codefx.libfx.control.properties.ControlPropertyListener;
import org.codefx.libfx.control.properties.TypeCheckingControlPropertyListener;

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
