package org.codefx.libfx.nesting;

import static org.junit.Assert.fail;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.testhelper.SomeValue;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

/**
 * Tests the {@link Nesting} implementation {@link ShallowNesting}.
 */
@RunWith(NestedRunner.class)
public class ShallowNestingTest {

	/**
	 * Tests a {@link ShallowNesting} based on an {@link Observable}.
	 */
	public static class OnObservable extends AbstractShallowNestingTest<Observable> {

		@Override
		protected Observable createNewNestingHierarchy() {
			/*
			 * To return an implementation of the 'Observable' interface which does not also implement other interfaces,
			 * create an anonymous class. It is assumed that listeners are neither added nor removed.
			 */
			return new Observable() {

				@Override
				public void addListener(InvalidationListener arg0) {
					fail();
				}

				@Override
				public void removeListener(InvalidationListener arg0) {
					fail();
				}

			};
		}

	}

	/**
	 * Tests a {@link ShallowNesting} based on an {@link ObservableValue}.
	 */
	public static class OnObservableValue
			extends AbstractShallowNestingTest<ObservableValue<SomeValue>> {

		@Override
		protected ObservableValue<SomeValue> createNewNestingHierarchy() {
			final SomeValue someValue = new SomeValue();

			/*
			 * To return an implementation of the 'ObservableValue' interface which does not also implement other
			 * interfaces, create an anonymous class. It is assumed that listeners are neither added nor removed.
			 */
			return new ObservableValue<SomeValue>() {

				@Override
				public void addListener(InvalidationListener listener) {
					fail();
				}

				@Override
				public void removeListener(InvalidationListener listener) {
					fail();
				}

				@Override
				public void addListener(ChangeListener<? super SomeValue> listener) {
					fail();
				}

				@Override
				public void removeListener(ChangeListener<? super SomeValue> listener) {
					fail();
				}

				@Override
				public SomeValue getValue() {
					return someValue;
				}
			};
		}
	}

	/**
	 * Tests a {@link ShallowNesting} based on a {@link Property}.
	 */
	public static class OnProperty extends AbstractShallowNestingTest<Property<SomeValue>> {

		@Override
		protected Property<SomeValue> createNewNestingHierarchy() {
			return new SimpleObjectProperty<SomeValue>(new SomeValue());
		}

	}

}
