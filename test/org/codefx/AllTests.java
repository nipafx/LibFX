package org.codefx;

import org.codefx.nesting.AllNestingTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
	AllNestingTests.class,
})
public class AllTests {
	// no body needed
}
