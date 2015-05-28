package org.codefx.libfx.nesting.property;

import java.util.Optional;
import java.util.function.Supplier;

import javafx.beans.property.Property;

/**
 * Internal specification of how a nested property behaves when the inner observable is missing.
 *
 * @param <T>
 *            the type contained in the nested property, e.g. {@link Integer} for {@link NestedIntegerProperty}
 */
interface InnerObservableMissingBehavior<T> {

	/**
	 * @return the behavior when the inner observable goes missing
	 */
	WhenInnerObservableGoesMissing whenGoesMissing();

	/**
	 * @return a supplier which will produce a value in the case that the inner observable goes missing as an
	 *         {@link Optional}
	 */
	Optional<? extends Supplier<T>> valueForMissing();

	/**
	 * @return behavior when the nested property is updated while the inner observable is missing
	 */
	WhenInnerObservableMissingOnUpdate onUpdate();

	/**
	 * Behavior when the inner observable goes missing.
	 */
	public enum WhenInnerObservableGoesMissing {

		/**
		 * The nested property will keep its value.
		 */
		KEEP_VALUE,

		/**
		 * The nested property will change to the default value for the wrapped type.
		 */
		SET_DEFAULT_VALUE,

		/**
		 * The nested property will change to the value which is specified by the supplier.
		 */
		SET_VALUE_FROM_SUPPLIER

	}

	/**
	 * Behavior when {@link Property#setValue(Object) setValue} is called while the inner observable is missing.
	 */
	public enum WhenInnerObservableMissingOnUpdate {

		/**
		 * The nested property will throw an exception.
		 */
		THROW_EXCEPTION,

		/**
		 * The nested property will accept the value but it will be overwritten when the next inner observable is set.
		 */
		ACCEPT_VALUE_UNTIL_NEXT_INNER_OBSERVABLE,

	}
}
