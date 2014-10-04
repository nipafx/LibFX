package org.codefx.libfx.control;

import static org.junit.Assert.fail;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the {@link ControlPropertyListenerBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		ControlPropertiesTest.BuilderContract.class,
		ControlPropertiesTest.CreatedCastingControlPropertyListener.class,
		ControlPropertiesTest.CreatedTypeCheckingControlPropertyListener.class })
public class ControlPropertiesTest {

	/**
	 * Tests the builder's contract.
	 */
	public static class BuilderContract {

		// #region ATTRIBUTES

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

		// #end ATTRIBUTES

		/**
		 * Initializes the attributes.
		 */
		@Before
		public void setUp() {
			properties = FXCollections.observableHashMap();
		}

		// #region TESTS

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
					.build();
		}

		/**
		 * Tests whether building with a missing key value processor the correct exception.
		 */
		@Test(expected = IllegalStateException.class)
		public void testIllegalStateExceptionOnBuildWithoutValueProcessor() {
			ControlProperties.on(properties)
					.forKey(KEY)
					.build();
		}

		// SUCCESSFUL CONSTRUCTION

		/**
		 * Tests whether building is successful when the minimum of attributes is set.
		 */
		public void testSuccessfulBuild() {
			ControlProperties.<String> on(properties)
					.forKey(KEY)
					.processValue(VOID_PROCESSOR)
					.build();
		}

		/**
		 * Tests whether building with a value type works as well.
		 */
		public void testSuccessfulBuildWithValueType() {
			ControlProperties.<String> on(properties)
					.forKey(KEY)
					.forValueType(String.class)
					.processValue(VOID_PROCESSOR)
					.build();
		}

		// #end TESTS

	}

	// #region TESTS CREATED LISTENERS

	/**
	 * Tests the created {@link CastingControlPropertyListener}.
	 */
	public static class CreatedCastingControlPropertyListener extends AbstractControlPropertyListenerTest {

		@Override
		protected <T> ControlPropertyListener createListener(
				ObservableMap<Object, Object> properties, Object key,
				Class<T> valueType, Consumer<T> valueProcessor) {

			ControlPropertyListenerBuilder<T> builder = ControlProperties.<T> on(properties)
					.forKey(key)
					.processValue(valueProcessor);

			// in order to create a casting listener, do not set the builder type;

			// make to check whether the correct type was created
			ControlPropertyListener listener = builder.build();
			if (!(listener instanceof CastingControlPropertyListener))
				fail();

			return listener;
		}

	}

	/**
	 * Tests the created {@link TypeCheckingControlPropertyListener}.
	 */
	public static class CreatedTypeCheckingControlPropertyListener extends AbstractControlPropertyListenerTest {

		@Override
		protected <T> ControlPropertyListener createListener(
				ObservableMap<Object, Object> properties, Object key,
				Class<T> valueType, Consumer<T> valueProcessor) {

			ControlPropertyListenerBuilder<T> builder = ControlProperties.<T> on(properties)
					.forKey(key)
					.forValueType(valueType)
					.processValue(valueProcessor);

			// make to check whether the correct type was created
			ControlPropertyListener listener = builder.build();
			if (!(listener instanceof TypeCheckingControlPropertyListener))
				fail();

			return listener;
		}

	}

	// #end TESTS CREATED LISTENERS

}
