package org.codefx.libfx.nesting.listener;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedInvalidationListenerBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedInvalidationListenerBuilderTest.Builder.class,
		NestedInvalidationListenerBuilderTest.CreatedListeners.class,
})
public class NestedInvalidationListenerBuilderTest {

	/**
	 * Tests whether the builder behaves well.
	 */
	public static class Builder extends AbstractNestedInvalidationListenerBuilderTest {

		@Override
		protected NestedInvalidationListenerBuilder createBuilder() {
			StringProperty innerObservable = new SimpleStringProperty();
			EditableNesting<StringProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedInvalidationListenerBuilder.forNesting(nesting);
		}

	}

	/**
	 * Tests whether the created listeners behave well.
	 */
	public static class CreatedListeners extends AbstractNestedInvalidationListenerTest {

		@Override
		protected NestedInvalidationListener createNestedListener(
				EditableNesting<? extends Observable> nesting, InvalidationListener listener) {

			return NestedInvalidationListenerBuilder
					.forNesting(nesting)
					.withListener(listener)
					.build();
		}

	}

}
