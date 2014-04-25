package org.codefx.nesting;

import javafx.beans.value.ObservableValue;

/**
 * Nestings is a helper class which makes creating nested properties, bindings and listeners more readable.
 */
public class Nestings {

	/**
	 * Starts a nesting with the specified outer observable value.
	 * 
	 * @param <T>
	 *            the type the outer observable value wraps
	 * @param outerObservableValue
	 *            the outer {@link ObservableValue} on which the nesting begins
	 * @return an instance of {@link NestingBuilder} which depends on the specified outer observable value
	 */
	public static <T> NestingBuilder<T> on(ObservableValue<T> outerObservableValue) {
		return NestingBuilder.on(outerObservableValue);
	}

}
