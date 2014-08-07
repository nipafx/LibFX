package org.codefx.libfx.nesting.listener;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;

/**
 * Tests the class {@link NestedInvalidationListener}.
 */
public class NestedInvalidationListenerTest extends AbstractNestedInvalidationListenerTest {

	@Override
	protected NestedInvalidationListener createNestedListener(
			EditableNesting<? extends Observable> nesting, InvalidationListener listener) {

		return new NestedInvalidationListener(nesting, listener);
	}

}
