package org.codefx.libfx.control.properties;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Random;
import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import org.codefx.libfx.control.properties.ControlPropertyListener;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to all tests of {@link ControlPropertyListener}.
 */
public abstract class AbstractControlPropertyListenerTest {

	// #region ATTRIBUTES

	/**
	 * A key to which the created listeners listen.
	 */
	private static final Object LISTENED_KEY = "listened";

	/**
	 * A key which the created listeners ignore.
	 */
	private static final Object IGNORED_KEY = "ignored";

	/**
	 * The property map to which the listeners listen.
	 */
	private ObservableMap<Object, Object> properties;

	// #end ATTRIBUTES

	// #region SETUP

	/**
	 * Initializes attributes for tests.
	 */
	@Before
	public void setUp() {
		properties = FXCollections.observableHashMap();
	}

	/**
	 * Creates the tested {@link ControlPropertyListener} from the specified arguments.
	 *
	 * @param <T>
	 *            the type of values which the listener processes
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @param key
	 *            the key to which the listener will listen
	 * @param valueType
	 *            the type of values which the listener processes
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's values
	 * @return whether the value could be processed or not
	 */
	protected abstract <T> ControlPropertyListener createListener(
			ObservableMap<Object, Object> properties, Object key,
			Class<T> valueType, Consumer<T> valueProcessor);

	/**
	 * Creates the tested {@link ControlPropertyListener} form the specified arguments, using {@link #properties} and
	 * {@link #LISTENED_KEY} as default values.
	 *
	 * @param <T>
	 *            the type of values which the listener processes
	 * @param valueType
	 *            the type of values which the listener processes
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's values
	 * @return whether the value could be processed or not
	 */
	private <T> ControlPropertyListener createDefaultListener(Class<T> valueType, Consumer<T> valueProcessor) {
		return createListener(properties, LISTENED_KEY, valueType, valueProcessor);
	}

	// #end SETUP

	// #region TESTS

	/**
	 * Tests whether the listener correctly processes a value for the correct key.
	 */
	@Test
	public void testSettingListenedKeyOnce() {
		// setup
		Property<String> listenedValue = new SimpleStringProperty();
		ControlPropertyListener listener = createDefaultListener(String.class, value -> listenedValue.setValue(value));
		listener.attach();

		// put a value
		String addedValue = "This value is put into the map.";
		properties.put(LISTENED_KEY, addedValue);

		// check
		assertSame(addedValue, listenedValue.getValue());
	}

	/**
	 * Tests whether the listener correctly processes setting the same value multiple times for the correct key.
	 */
	@Test
	public void testSettingListenedKeyRepeatedly() {
		// setup
		Property<String> listenedValue = new SimpleStringProperty();
		ControlPropertyListener listener = createDefaultListener(String.class, value -> listenedValue.setValue(value));
		listener.attach();

		// put and check the same value over and over
		String addedValue = "This value is put into the map.";
		for (int i = 0; i < 10; i++) {
			// put and check
			properties.put(LISTENED_KEY, addedValue);
			assertSame(addedValue, listenedValue.getValue());
			// reset the property to null to see whether it is really set the next time
			listenedValue.setValue(null);
		}
	}

	/**
	 * Tests whether the listener correctly processes setting multiple random values.
	 */
	@Test
	public void testSettingListenedKeyRandomly() {
		// setup
		Property<String> listenedValue = new SimpleStringProperty();
		ControlPropertyListener listener = createDefaultListener(String.class, value -> listenedValue.setValue(value));
		listener.attach();

		// put and check some random values
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			// create a random string
			byte[] bytes = new byte[256];
			random.nextBytes(bytes);
			String addedValue = new String(bytes);
			// put and check
			properties.put(LISTENED_KEY, addedValue);
			assertSame(addedValue, listenedValue.getValue());
		}
	}

	/**
	 * Tests whether the listener ignores values of the wrong type.
	 */
	@Test
	public void testSettingListenedKeyOfWrongType() {
		// setup
		Consumer<Integer> failsTestIfCalled = value -> fail();
		ControlPropertyListener listener = createDefaultListener(Integer.class, failsTestIfCalled);
		listener.attach();

		// put a value of the wrong type
		properties.put(LISTENED_KEY, "some non integer");
	}

	/**
	 * Tests whether the listener ignores values for other keys.
	 */
	@Test
	public void testSettingIgnoredKey() {
		// setup
		Consumer<String> failsTestIfCalled = value -> fail();
		ControlPropertyListener listener = createDefaultListener(String.class, failsTestIfCalled);
		listener.attach();

		// put a value of the wrong type
		properties.put(IGNORED_KEY, "some value");
	}

	/**
	 * Tests whether the listener correctly processes a value which already existed in the map before it was attached.
	 */
	@Test
	public void testProcessingPresentValueOnAttach() {
		// setup
		Property<String> listenedValue = new SimpleStringProperty();
		ControlPropertyListener listener = createDefaultListener(String.class, value -> listenedValue.setValue(value));
		String existingValue = "some existing value";
		properties.put(LISTENED_KEY, existingValue);

		// this should trigger processing the value
		listener.attach();

		assertSame(existingValue, listenedValue.getValue());
	}

	/**
	 * Tests whether the listener ignores values after it was detached.
	 */
	@Test
	public void testSettingListenedKeyAfterDetach() {
		// setup
		Consumer<String> failsTestIfCalled = value -> fail();
		ControlPropertyListener listener = createDefaultListener(String.class, failsTestIfCalled);
		listener.attach();
		listener.detach();

		// put a value of the wrong type
		properties.put(LISTENED_KEY, "some value");
	}

	/**
	 * Tests whether the listener processes values after it was detached and then reattached.
	 */
	@Test
	public void testSettingListenedAfterReattach() {
		// setup
		Property<String> listenedValue = new SimpleStringProperty();
		ControlPropertyListener listener = createDefaultListener(String.class, value -> listenedValue.setValue(value));
		listener.attach();
		listener.detach();
		listener.attach();

		// put a value
		String addedValue = "This value is put into the map.";
		properties.put(LISTENED_KEY, addedValue);

		// check
		assertSame(addedValue, listenedValue.getValue());
	}

	// #end TESTS

}
