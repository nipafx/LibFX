package org.codefx.libfx;

import org.codefx.libfx.control._AllControlTests;
import org.codefx.libfx.nesting._AllNestingTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
		_AllControlTests.class,
		_AllNestingTests.class,
})
public class AllTests {
	// no body needed
}
