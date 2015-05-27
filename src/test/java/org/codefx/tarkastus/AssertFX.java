package org.codefx.tarkastus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Moar assertions!
 */
public class AssertFX {

	/**
	 * Asserts that the two values are the same for reference types or equal for primitives (using {@code isPrimitive}
	 * to discern).
	 *
	 * @param <T>
	 *            the type of the compared instances
	 * @param expected
	 *            expected value
	 * @param actual
	 *            the value to check against expected
	 * @param isPrimitive
	 *            indicates whether the compared type is a primitive
	 */
	public static <T> void assertSameOrEqual(T expected, T actual, boolean isPrimitive) {
		if (isPrimitive)
			assertEquals(expected, actual);
		else
			assertSame(expected, actual);
	}

	/**
	 * If the specified value is of a primitive wrapping type (e.g. {@link Integer}), a call asserts that it is the
	 * default value for that primitive; otherwise the value must be null.
	 *
	 * @param value
	 *            the value to check
	 */
	public static void assertDefault(Object value) {
		if (value instanceof Boolean)
			assertFalse((Boolean) value);
		else if (value instanceof Integer)
			assertEquals(0, ((Integer) value).intValue());
		else if (value instanceof Long)
			assertEquals(0, ((Long) value).longValue());
		else if (value instanceof Float)
			assertEquals(0, ((Float) value).floatValue(), 0);
		else if (value instanceof Double)
			assertEquals(0, ((Double) value).doubleValue(), 0);
		else
			assertNull(value);
	}

}
