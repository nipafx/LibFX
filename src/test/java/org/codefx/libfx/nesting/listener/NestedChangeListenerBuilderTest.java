package org.codefx.libfx.nesting.listener;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.listener.handle.CreateListenerHandle;
import org.codefx.libfx.nesting.Nesting;
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
		NestedChangeListenerBuilderTest.CreatedListenerHandles.class,
})
public class NestedChangeListenerBuilderTest {

	/**
	 * Tests whether the builder behaves well.
	 */
	public static class Builder extends AbstractNestedChangeListenerBuilderTest {

		@Override
		protected <T> NestedChangeListenerBuilder<T, Property<T>> createBuilder() {
			Property<T> innerObservable = new SimpleObjectProperty<>();
			EditableNesting<Property<T>> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedChangeListenerBuilder.forNesting(nesting);
		}

	}

	/**
	 * Tests whether the created listener handles behave well.
	 */
	public static class CreatedListenerHandles extends AbstractNestedChangeListenerHandleTest {

		@Override
		protected <T> NestedChangeListenerHandle<T> createNestedListenerHandle(
				Nesting<? extends ObservableValue<T>> nesting,
				ChangeListener<T> listener,
				CreateListenerHandle attachedOrDetached) {

			NestedChangeListenerBuilder<T, ? extends ObservableValue<T>>.Buildable builder =
					NestedChangeListenerBuilder
							.forNesting(nesting)
							.withListener(listener);

			if (attachedOrDetached == CreateListenerHandle.ATTACHED)
				return builder.build();
			else if (attachedOrDetached == CreateListenerHandle.DETACHED)
				return builder.buildDetached();
			else
				throw new IllegalArgumentException();
		}

	}

}
