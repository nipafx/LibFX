package org.codefx.libfx.nesting.listener;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.codefx.libfx.listener.handle.CreateListenerHandle;
import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

/**
 * Tests the class {@link NestedInvalidationListenerBuilder}.
 */
@RunWith(NestedRunner.class)
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
	public static class CreatedListeners extends AbstractNestedInvalidationListenerHandleTest {

		@Override
		protected NestedInvalidationListenerHandle createNestedListenerHandle(
				Nesting<? extends Observable> nesting,
				InvalidationListener listener,
				CreateListenerHandle attachedOrDetached) {

			NestedInvalidationListenerBuilder.Buildable builder =
					NestedInvalidationListenerBuilder
							.forNesting(nesting)
							.withListener(listener);

			if (attachedOrDetached == CreateListenerHandle.ATTACHED)
				return builder.buildAttached();
			else if (attachedOrDetached == CreateListenerHandle.DETACHED)
				return builder.buildDetached();
			else
				throw new IllegalArgumentException();
		}

	}

}
