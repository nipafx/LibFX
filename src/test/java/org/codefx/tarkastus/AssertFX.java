package org.codefx.tarkastus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

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

	/**
	 * Asserts that executing the specified operation throws an {@link UnsupportedOperationException}.
	 * <p>
	 * The assertion fails if the operation throws any other exception or nor exception at all.
	 *
	 * @param operation
	 *            the operation to test
	 */
	public static void assertOperationIsUnsupported(RunnableWithException operation) {
		try {
			operation.run();
			// there should have been an 'UnsupportedOperationException' so fail the test if there wasn't
			fail("");
		} catch (UnsupportedOperationException ex) {
			// this is the intended exception; if it is thrown, the test passes
		} catch (Exception ex) {
			// this exception is not inteded, so the test should fail if it is thrown
			fail();
		}
	}

	@FunctionalInterface
	public interface RunnableWithException {
		void run() throws Exception;
	}

}
