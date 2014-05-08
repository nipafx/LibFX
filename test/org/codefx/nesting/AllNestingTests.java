package org.codefx.nesting;

import org.codefx.nesting.property.AllNestedPropertyTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests in this package and its subpackages.
 */
@RunWith(Suite.class)
@SuiteClasses({
	AllNestedPropertyTests.class,
	DeepNestingTest.class,
})
public class AllNestingTests {
	// no body needed
}
