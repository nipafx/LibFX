package org.codefx.libfx;

import org.codefx.libfx.concurrent._AllConcurrentTests;
import org.codefx.libfx.control.properties._AllPropertiesTests;
import org.codefx.libfx.dom._AllDomTests;
import org.codefx.libfx.nesting._AllNestingTests;
import org.codefx.libfx.serialization._AllSerializationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
		_AllConcurrentTests.class,
		_AllDomTests.class,
		_AllNestingTests.class,
		_AllPropertiesTests.class,
		_AllSerializationTests.class,
})
public class AllTests {
	// no body needed
}
