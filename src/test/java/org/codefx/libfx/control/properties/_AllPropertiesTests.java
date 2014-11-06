package org.codefx.libfx.control.properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests in this package and its subpackages.
 */
@RunWith(Suite.class)
@SuiteClasses({
		ControlPropertiesTest.class,
		CastingControlPropertyListenerHandleTest.class,
		TypeCheckingControlPropertyListenerHandleTest.class })
public class _AllPropertiesTests {
	// no body needed
}
