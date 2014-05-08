package org.codefx.nesting.property;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests in this package and its subpackages.
 */
@RunWith(Suite.class)
@SuiteClasses({
	NestedIntegerPropertyTest.class,
	NestedObjectPropertyTest.class,
})
public class AllNestedPropertyTests {
	// no body needed
}
