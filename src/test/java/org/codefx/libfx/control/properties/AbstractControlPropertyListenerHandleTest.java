package org.codefx.libfx.control.properties;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Random;
import java.util.function.Consumer;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import org.codefx.libfx.listener.handle.CreateListenerHandle;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to all tests of {@link ControlPropertyListenerHandle}.
 */
public abstract class AbstractControlPropertyListenerHandleTest {

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

	/**
	 * The default value processor. Will be mocked to verify interactions.
	 */
	private Consumer<String> valueProcessor;

	/**
	 * A value processor which fails the test if it is called.
	 */
	private Consumer<String> valueProcessorWhichFailsTestWhenCalled;

	// #end ATTRIBUTES

	// #region SETUP

	/**
	 * Initializes attributes for tests.
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		properties = FXCollections.observableHashMap();
		valueProcessor = mock(Consumer.class);
		valueProcessorWhichFailsTestWhenCalled = any -> fail();
	}

	/**
	 * Creates the tested {@link ControlPropertyListenerHandle} from the specified arguments.
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
	 * @param attachedOrDetached
	 *            indicates whether the created handle will be initially attached or detached
	 * @return the created {@link ControlPropertyListenerHandle}
	 */
	protected abstract <T> ControlPropertyListenerHandle createListener(
			ObservableMap<Object, Object> properties, Object key,
			Class<T> valueType, Consumer<? super T> valueProcessor, CreateListenerHandle attachedOrDetached);

	/**
	 * Creates the tested {@link ControlPropertyListenerHandle}. It will operate on {@link #properties}, listen to
	 * {@link #LISTENED_KEY} and values of type {@link String}. The created listener is initially
	 * {@link CreateListenerHandle#DETACHED detached}.
	 *
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's string values
	 * @return the created {@link ControlPropertyListenerHandle}
	 */
	private ControlPropertyListenerHandle createDetachedDefaultListener(Consumer<? super String> valueProcessor) {
		return createListener(properties, LISTENED_KEY, String.class, valueProcessor, CreateListenerHandle.DETACHED);
	}

	/**
	 * Creates the tested {@link ControlPropertyListenerHandle}. It will operate on {@link #properties}, listen to
	 * {@link #LISTENED_KEY} and values of type {@link String}. The created listener is initially
	 * {@link CreateListenerHandle#ATTACHED attached}.
	 *
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's string values
	 * @return the created {@link ControlPropertyListenerHandle}
	 */
	private ControlPropertyListenerHandle createAttachedDefaultListener(Consumer<? super String> valueProcessor) {
		return createListener(properties, LISTENED_KEY, String.class, valueProcessor, CreateListenerHandle.ATTACHED);
	}

	// #end SETUP

	// #region TESTS

	/**
	 * Tests whether the listener correctly processes a value for the correct key if the listener is initially attached.
	 */
	@Test
	public void testSettingListenedKeyOnceWhenInitiallyAttached() {
		// setup
		createAttachedDefaultListener(valueProcessor);

		// put a value
		String addedValue = "This value is put into the map.";
		properties.put(LISTENED_KEY, addedValue);

		// check
		verify(valueProcessor, times(1)).accept(addedValue);
		verifyNoMoreInteractions(valueProcessor);
	}

	/**
	 * Tests whether the listener correctly processes a value for the correct key.
	 */
	@Test
	public void testSettingListenedKeyOnceWhenAttachedAfterConstruction() {
		// setup
		ControlPropertyListenerHandle listenerHandle = createDetachedDefaultListener(valueProcessor);
		listenerHandle.attach();

		// put a value
		String addedValue = "This value is put into the map.";
		properties.put(LISTENED_KEY, addedValue);

		// check
		verify(valueProcessor, times(1)).accept(addedValue);
		verifyNoMoreInteractions(valueProcessor);
	}

	/**
	 * Tests whether the listener correctly processes setting the same value multiple times for the correct key.
	 */
	@Test
	public void testSettingListenedKeyRepeatedly() {
		// setup
		createAttachedDefaultListener(valueProcessor);

		// put the same value over and over
		String addedValue = "This value is put into the map.";
		for (int i = 0; i < 10; i++)
			properties.put(LISTENED_KEY, addedValue);

		// check
		verify(valueProcessor, times(10)).accept(addedValue);
		verifyNoMoreInteractions(valueProcessor);
	}

	/**
	 * Tests whether the listener correctly processes setting multiple random values.
	 */
	@Test
	public void testSettingListenedKeyRandomly() {
		// setup
		Property<String> listenedValue = new SimpleStringProperty();
		createAttachedDefaultListener(listenedValue::setValue);

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
		Consumer<Integer> valueProcessorWhichFailsTestWhenCalled = any -> fail();
		// setup
		createListener(properties, LISTENED_KEY, Integer.class,
				valueProcessorWhichFailsTestWhenCalled, CreateListenerHandle.ATTACHED);

		// put a value of the wrong type
		properties.put(LISTENED_KEY, "some non integer");
	}

	/**
	 * Tests whether the listener ignores values for other keys.
	 */
	@Test
	public void testSettingIgnoredKey() {
		// setup
		ControlPropertyListenerHandle listenerHandle =
				createDetachedDefaultListener(valueProcessorWhichFailsTestWhenCalled);
		listenerHandle.attach();

		// put a value for a key to which the listener does not listen
		properties.put(IGNORED_KEY, "some value");
	}

	/**
	 * Tests whether the listener correctly processes a value which already existed in the map before it was attached.
	 */
	@Test
	public void testProcessingPresentValueOnAttach() {
		// setup
		ControlPropertyListenerHandle listenerHandle = createDetachedDefaultListener(valueProcessor);
		String existingValue = "some existing value";
		properties.put(LISTENED_KEY, existingValue);

		// this should trigger processing the value
		listenerHandle.attach();

		verify(valueProcessor, times(1)).accept(existingValue);
		verifyNoMoreInteractions(valueProcessor);
	}

	/**
	 * Tests whether the listener ignores values after it was detached.
	 */
	@Test
	public void testDetachWhenInitiallyAttached() {
		// setup
		ControlPropertyListenerHandle listenerHandle =
				createAttachedDefaultListener(valueProcessorWhichFailsTestWhenCalled);
		listenerHandle.detach();

		// put a value of the correct type for the listened key
		properties.put(LISTENED_KEY, "some value");
	}

	/**
	 * Tests whether the listener ignores values after it was detached.
	 */
	@Test
	public void testDetachAfterAttach() {
		// setup
		ControlPropertyListenerHandle listenerHandle =
				createDetachedDefaultListener(valueProcessorWhichFailsTestWhenCalled);
		listenerHandle.attach();
		listenerHandle.detach();

		// put a value of the correct type for the listened key
		properties.put(LISTENED_KEY, "some value");
	}

	/**
	 * Tests whether the listener ignores values after it was detached repeatedly.
	 */
	@Test
	public void testMultipleDetach() {
		// setup
		ControlPropertyListenerHandle listenerHandle =
				createDetachedDefaultListener(valueProcessorWhichFailsTestWhenCalled);
		listenerHandle.attach();
		listenerHandle.detach();
		listenerHandle.detach();
		listenerHandle.detach();

		// put a value of the correct type for the listened key
		properties.put(LISTENED_KEY, "some value");
	}

	/**
	 * Tests whether the listener processes values after it was detached and then reattached.
	 */
	@Test
	public void testReattach() {
		// setup
		ControlPropertyListenerHandle listenerHandle = createDetachedDefaultListener(valueProcessor);
		listenerHandle.attach();
		listenerHandle.detach();
		listenerHandle.attach();

		// put a value of the correct type for the listened key
		String addedValue = "This value is put into the map.";
		properties.put(LISTENED_KEY, addedValue);

		// check
		verify(valueProcessor, times(1)).accept(addedValue);
		verifyNoMoreInteractions(valueProcessor);
	}

	/**
	 * Tests whether the listener is only called once even when attached is called repeatedly.
	 */
	@Test
	public void testMultipleAttach() {
		ControlPropertyListenerHandle listenerHandle = createDetachedDefaultListener(valueProcessor);
		listenerHandle.attach();
		listenerHandle.attach();
		listenerHandle.attach();

		// put a value of the correct type for the listened key
		String addedValue = "Some value...";
		properties.put(LISTENED_KEY, addedValue);

		// check
		verify(valueProcessor, times(1)).accept(addedValue);
		verifyNoMoreInteractions(valueProcessor);
	}

	// #end TESTS

}
