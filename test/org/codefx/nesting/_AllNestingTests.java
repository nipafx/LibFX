package org.codefx.nesting;

import org.codefx.nesting.property._AllNestedPropertyTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests in this package and its subpackages.
 */
@RunWith(Suite.class)
@SuiteClasses({
		_AllNestedPropertyTests.class,
		DeepNestingTest.class,
		ShallowNestingTest.class,
})
public class _AllNestingTests {
	// no body needed
}
