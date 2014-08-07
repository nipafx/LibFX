package org.codefx.libfx.nesting.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;

/**
 * Tests the class {@link NestedChangeListener}.
 */
public class NestedChangeListenerTest extends AbstractNestedChangeListenerTest {

	@Override
	protected NestedChangeListener<String> createNestedListener(
			EditableNesting<? extends ObservableValue<String>> nesting, ChangeListener<String> listener) {

		return new NestedChangeListener<String>(nesting, listener);
	}

}
