package org.codefx.libfx.control.properties;

import static org.junit.Assert.fail;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import org.codefx.libfx.listener.handle.CreateListenerHandle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

/**
 * Tests the {@link ControlPropertyListenerBuilder}.
 */
@RunWith(NestedRunner.class)
public class ControlPropertiesTest {

	/**
	 * Tests the builder's contract.
	 */
	public static class BuilderContract {

		// #begin FIELDS

		/**
		 * The key used to create the listeners.
		 */
		private static final Object KEY = "key";

		/**
		 * The processor which can be used when nothing needs to happen.
		 */
		private static final Consumer<Object> VOID_PROCESSOR = value -> {/* do nothing */};

		/**
		 * The property map used to create the listeners.
		 */
		private ObservableMap<Object, Object> properties;

		// #end FIELDS

		/**
		 * Initializes the fields.
		 */
		@Before
		public void setUp() {
			properties = FXCollections.observableHashMap();
		}

		// #begin TESTS

		// EXCEPTIONS DURING CONSTRUCTION

		/**
		 * Tests whether starting on a null map throws the correct exception.
		 */
		@Test(expected = NullPointerException.class)
		public void testNullPointerExceptionOnNullMap() {
			ControlProperties.on(null);
		}

		/**
		 * Tests whether using a null key throws the correct exception.
		 */
		@Test(expected = NullPointerException.class)
		public void testNullPointerExceptionOnNullKey() {
			ControlProperties.on(properties)
					.forKey(null);
		}

		/**
		 * Tests whether using a null value type throws the correct exception.
		 */
		@Test(expected = NullPointerException.class)
		public void testNullPointerExceptionOnNullValueType() {
			ControlProperties.on(properties)
					.forValueType(null);
		}

		/**
		 * Tests whether using a null value processor throws the correct exception.
		 */
		@Test(expected = NullPointerException.class)
		public void testNullPointerExceptionOnNullValueProcessor() {
			ControlProperties.on(properties)
					.processValue(null);
		}

		/**
		 * Tests whether building with a missing key throws the correct exception.
		 */
		@Test(expected = IllegalStateException.class)
		public void testIllegalStateExceptionOnBuildWithoutKey() {
			ControlProperties.on(properties)
					.processValue(VOID_PROCESSOR)
					.buildDetached();
		}

		/**
		 * Tests whether building with a missing key value processor the correct exception.
		 */
		@Test(expected = IllegalStateException.class)
		public void testIllegalStateExceptionOnBuildWithoutValueProcessor() {
			ControlProperties.on(properties)
					.forKey(KEY)
					.buildDetached();
		}

		// SUCCESSFUL CONSTRUCTION

		/**
		 * Tests whether building is successful when the minimum of values is set.
		 */
		public void testSuccessfulBuild() {
			ControlProperties.<String> on(properties)
					.forKey(KEY)
					.processValue(VOID_PROCESSOR)
					.buildDetached();
		}

		/**
		 * Tests whether building with a value type works as well.
		 */
		public void testSuccessfulBuildWithValueType() {
			ControlProperties.<String> on(properties)
					.forKey(KEY)
					.forValueType(String.class)
					.processValue(VOID_PROCESSOR)
					.buildDetached();
		}

		// #end TESTS

	}

	// #begin TESTS CREATED LISTENERS

	/**
	 * Tests the created {@link CastingControlPropertyListenerHandle}.
	 */
	public static class CreatedCastingControlPropertyListener extends AbstractControlPropertyListenerHandleTest {

		@Override
		protected <T> ControlPropertyListenerHandle createListener(
				ObservableMap<Object, Object> properties, Object key,
				Class<T> valueType, Consumer<? super T> valueProcessor,
				CreateListenerHandle attachedOrDetached) {

			// parameterize the builder
			ControlPropertyListenerBuilder<T> builder = ControlProperties.<T> on(properties)
					.forKey(key)
					.processValue(valueProcessor);
			// in order to create a casting listener, do not set the builder type;

			// create the listener according to 'attachedOrDetached'
			ControlPropertyListenerHandle listener;
			if (attachedOrDetached == CreateListenerHandle.ATTACHED)
				listener = builder.buildAttached();
			else if (attachedOrDetached == CreateListenerHandle.DETACHED)
				listener = builder.buildDetached();
			else
				throw new IllegalArgumentException();

			// check whether the correct type was created
			if (!(listener instanceof CastingControlPropertyListenerHandle))
				fail();

			return listener;
		}

	}

	/**
	 * Tests the created {@link TypeCheckingControlPropertyListenerHandle}.
	 */
	public static class CreatedTypeCheckingControlPropertyListener extends AbstractControlPropertyListenerHandleTest {

		@Override
		protected <T> ControlPropertyListenerHandle createListener(
				ObservableMap<Object, Object> properties, Object key,
				Class<T> valueType, Consumer<? super T> valueProcessor,
				CreateListenerHandle attachedOrDetached) {

			// parameterize the builder
			ControlPropertyListenerBuilder<T> builder = ControlProperties.<T> on(properties)
					.forKey(key)
					.forValueType(valueType)
					.processValue(valueProcessor);

			// create the listener according to 'attachedOrDetached'
			ControlPropertyListenerHandle listener;
			if (attachedOrDetached == CreateListenerHandle.ATTACHED)
				listener = builder.buildAttached();
			else if (attachedOrDetached == CreateListenerHandle.DETACHED)
				listener = builder.buildDetached();
			else
				throw new IllegalArgumentException();

			// check whether the correct type was created
			if (!(listener instanceof TypeCheckingControlPropertyListenerHandle))
				fail();

			return listener;
		}

	}

	// #end TESTS CREATED LISTENERS

}
