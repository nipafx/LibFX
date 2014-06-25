package org.codefx.libfx.nesting;

import static org.junit.Assert.fail;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.ShallowNesting;
import org.codefx.libfx.nesting.testhelper.SomeValue;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the {@link Nesting} implementation {@link ShallowNesting}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		ShallowNestingTest.OnObservable.class,
		ShallowNestingTest.OnObservableValue.class,
		ShallowNestingTest.OnProperty.class,
})
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
			/*
			 * TODO check whether it would be better to return an implementation of 'ObservableValue' which does not
			 * also implement other interfaces.
			 */
			return new SimpleObjectProperty<SomeValue>(new SomeValue());
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
