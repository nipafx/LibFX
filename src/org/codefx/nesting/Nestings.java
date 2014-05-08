package org.codefx.nesting;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

/**
 * Creates builders for nestings and allows using them in a fluent way. <h2>Nomenclature</h2>
 * <p>
 * outer -> nested -> inner (which is also nested) <br>
 * level 0 -> level 1 -> ...
 * <p>
 * TODO: nomenclature: "observable" = "observable value" TODO: fix names of all generics to some common schema like
 * O(outer) and N(ested)
 * <p>
 * TODO examples; differences between builder types
 */
public class Nestings {

	/**
	 * Starts a nesting with the specified outer observable.
	 *
	 * @param outerObservable
	 *            the outer {@link Observable} on which the nesting begins
	 * @return an instance of {@link ObservableNestingBuilder} which depends on the specified outer observable
	 */
	public static ObservableNestingBuilder on(Observable outerObservable) {
		return new ObservableNestingBuilder(outerObservable);
	}

	/**
	 * Starts a nesting with the specified outer observable.
	 *
	 * @param <T>
	 *            the type the outer observable wraps
	 * @param outerObservable
	 *            the outer {@link ObservableValue} on which the nesting begins
	 * @return an instance of {@link ObservableValueNestingBuilder} which depends on the specified outer observable
	 */
	public static <T> ObservableValueNestingBuilder<T> on(ObservableValue<T> outerObservable) {
		return new ObservableValueNestingBuilder<T>(outerObservable);
	}

	/**
	 * Starts a nesting with the specified outer property.
	 *
	 * @param <T>
	 *            the type the outer property wraps
	 * @param outerProperty
	 *            the outer {@link Property} on which the nesting begins
	 * @return an instance of {@link ObjectPropertyNestingBuilder} which depends on the specified outer observable value
	 */
	public static <T> ObjectPropertyNestingBuilder<T> on(Property<T> outerProperty) {
		return new ObjectPropertyNestingBuilder<T>(outerProperty);
	}

}
