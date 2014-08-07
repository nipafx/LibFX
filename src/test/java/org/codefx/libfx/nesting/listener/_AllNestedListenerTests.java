package org.codefx.libfx.nesting.listener;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests in this package and its subpackages.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedChangeListenerBuilderTest.class,
		NestedChangeListenerTest.class,
		NestedInvalidationListenerBuilderTest.class,
		NestedInvalidationListenerTest.class,
})
public class _AllNestedListenerTests {
	// no body needed
}
