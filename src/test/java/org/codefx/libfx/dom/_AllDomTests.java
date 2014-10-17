package org.codefx.libfx.dom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests in this package.
 */
@RunWith(Suite.class)
@SuiteClasses({
		DefaultEventTransformerTest.class,
		DomEventToHyperlinkEventTransformerTest.class,
		StaticEventTransformerTest.class,
})
public class _AllDomTests {
	// no body needed
}
