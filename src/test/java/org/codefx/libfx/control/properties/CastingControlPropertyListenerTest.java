package org.codefx.libfx.control.properties;

import java.util.function.Consumer;

import org.codefx.libfx.control.properties.CastingControlPropertyListener;
import org.codefx.libfx.control.properties.ControlPropertyListener;

import javafx.collections.ObservableMap;

/**
 * Tests {@link CastingControlPropertyListener}.
 */
public class CastingControlPropertyListenerTest extends AbstractControlPropertyListenerTest {

	@Override
	protected <T> ControlPropertyListener createListener(
			ObservableMap<Object, Object> properties, Object key,
			Class<T> valueType, Consumer<T> valueProcessor) {

		return new CastingControlPropertyListener<T>(properties, key, valueProcessor);
	}

}
