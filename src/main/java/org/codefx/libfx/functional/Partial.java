package org.codefx.libfx.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Partial {

	public static final Variable VAR = new Variable();

	/*
	 * PARTIAL FUNCTION APPLICATION
	 */

	// BiFunction

	public static <T, U, R> Function<U, R> partial(BiFunction<T, U, R> function, T fixedValue, Variable var) {
		return u -> function.apply(fixedValue, u);
	}

	public static <T, U, R> Function<T, R> partial(BiFunction<T, U, R> function, Variable var, U fixedValue) {
		return t -> function.apply(t, fixedValue);
	}

	/*
	 * PRIVATE CLASSES
	 */

	public static class Variable {

		private Variable() {
			// do nothing
		}

	}

}
