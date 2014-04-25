package org.codefx.nesting.property;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.Nesting;

/**
 * A property which has no value of its own but is always bound to the inner property of some {@link Nesting}.
 *
 * @param <T>
 *            the type of the wrapped value
 */
public final class NestedObjectProperty<T> extends SimpleObjectProperty<T> {

	/**
	 * Creates a new nested property, which uses the specified nesting. It belongs to the specified bean and has the
	 * specified name (one or both can be null).
	 *
	 * @param nesting
	 *            the nesting used by this nested property
	 * @param bean
	 *            the bean to which this nested property belongs
	 * @param name
	 *            this nested property's name
	 * @param defaultValueSupplier
	 *            creates a value for the {@code property} in case the {@code nesting's} property is null
	 */
	NestedObjectProperty(
			Nesting<? extends Property<T>> nesting, Object bean, String name, Supplier<T> defaultValueSupplier) {

		super(bean, name);

		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		PropertyToNestingBinding.bind(this, nesting, defaultValueSupplier);
	}

}
