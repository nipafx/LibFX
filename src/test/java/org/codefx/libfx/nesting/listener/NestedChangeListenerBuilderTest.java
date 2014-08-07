package org.codefx.libfx.nesting.listener;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedChangeListenerBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedChangeListenerBuilderTest.Builder.class,
		NestedChangeListenerBuilderTest.CreatedListeners.class,
})
public class NestedChangeListenerBuilderTest {

	/**
	 * Tests whether the builder behaves well.
	 */
	public static class Builder extends AbstractNestedChangeListenerBuilderTest {

		@Override
		protected NestedChangeListenerBuilder<String, StringProperty> createBuilder() {
			StringProperty innerObservable = new SimpleStringProperty();
			EditableNesting<StringProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedChangeListenerBuilder.forNesting(nesting);
		}

	}

	/**
	 * Tests whether the created listeners behave well.
	 */
	public static class CreatedListeners extends AbstractNestedChangeListenerTest {

		@Override
		protected NestedChangeListener<String> createNestedListener(
				EditableNesting<? extends ObservableValue<String>> nesting, ChangeListener<String> listener) {

			return NestedChangeListenerBuilder
					.forNesting(nesting)
					.withListener(listener)
					.build();
		}

	}

}
