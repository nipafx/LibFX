package org.codefx.libfx.functional;

import static org.codefx.libfx.functional.Partial.VAR;
import static org.codefx.libfx.functional.Partial.partial;
import static org.junit.Assert.assertEquals;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;

public class PartialTest {

	/*
	 * SYNTAX
	 */

	@Test
	public void testPartialWithMethodReference() {
		for (int a = 0; a < 10; a++) {
			Function<Integer, Integer> firstFixed = partial(PartialTest::multiply, a, VAR);
			for (int b = 0; b < 10; b++) {
				int expected = multiply(a, b);
				int result = firstFixed.apply(b);
				assertEquals(expected, result);
			}
		}
	}

	private static Integer multiply(Integer a, Integer b) {
		return a * b;
	}

	@Test
	public void testPartialWithConstructorReference() {
		for (int s = 0; s < 10; s++) {
			String str = "" + s;
			Function<Integer, Pair<Integer, String>> secondFixed = partial(Pair<Integer, String>::new, VAR, str);
			for (int i = 0; i < 10; i++) {
				Pair<Integer, String> expected = new Pair<>(i, str);
				Pair<Integer, String> result = secondFixed.apply(i);
				assertEquals(expected, result);
			}
		}
	}

	@SuppressWarnings("javadoc")
	private static class Pair<T, U> {

		private final T first;

		private final U second;

		public Pair(T first, U second) {
			super();
			this.first = first;
			this.second = second;
		}

		@Override
		public int hashCode() {
			return Objects.hash(first, second);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object obj) {
			// very optimistic equals!
			Pair other = (Pair) obj;
			return Objects.equals(first, other.first)
					&& Objects.equals(second, other.second);
		}

	}

	/*
	 * CORRECTNESS
	 */

	@Test
	public void testFixFirstInBiFunction() {
		BiFunction<Integer, Integer, Integer> f = (a, b) -> a * (b + 1);

		for (int a = 0; a < 10; a++) {
			Function<Integer, Integer> firstFixed = partial(f, a, VAR);
			for (int b = 0; b < 10; b++) {
				int expected = f.apply(a, b);
				int result = firstFixed.apply(b);
				assertEquals(expected, result);
			}
		}
	}

	@Test
	public void testFixSecondInBiFunction() {
		BiFunction<Integer, Integer, Integer> f = (a, b) -> a * (b + 1);

		for (int b = 0; b < 10; b++) {
			Function<Integer, Integer> secondFixed = partial(f, VAR, b);
			for (int a = 0; a < 10; a++) {
				int expected = f.apply(a, b);
				int result = secondFixed.apply(a);
				assertEquals(expected, result);
			}
		}
	}

}
