package org.codefx.nesting.property;

import java.util.function.Supplier;

import javafx.beans.property.Property;

import org.codefx.nesting.Nesting;

/**
 * A builder which creates a {@link NestedObjectProperty}.
 *
 * @param <T>
 *            the type of the value wrapped by the property which will be build
 */
public final class NestedObjectPropertyBuilder<T> extends AbstractNestedPropertyBuilder<Property<T>> {

	// #region PROPERTIES

	/**
	 * Provides a default value for the case that the nesting's inner property is null.
	 */
	private Supplier<T> defaultValueSupplier;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedObjectPropertyBuilder(Nesting<Property<T>> nesting) {
		super(nesting);
		defaultValueSupplier = () -> null;
	}

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param <T>
	 *            the type of the value wrapped by the property which will be build
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 * @return a new instance of {@link NestedObjectPropertyBuilder}
	 */
	public static <T> NestedObjectPropertyBuilder<T> forNesting(Nesting<Property<T>> nesting) {
		return new NestedObjectPropertyBuilder<>(nesting);
	}

	//#end CONSTRUCTION

	// #region METHODS

	@Override
	public Property<T> build() {
		return new NestedObjectProperty<>(getNesting(), getBean(), getName(), defaultValueSupplier);
	}

	//#end METHODS

	// #region PROPERTY ACCESS

	/**
	 * Sets the default value, which defines the property's value while it is not bound to
	 * {@link Nesting#innerProperty()}.
	 *
	 * @param defaultValue
	 *            the default value
	 */
	public void setDefaultValue(T defaultValue) {
		this.defaultValueSupplier = () -> defaultValue;
	}

	/**
	 * Sets the default value supplier, which defines the property's value while it is not bound to
	 * {@link Nesting#innerProperty()}.
	 *
	 * @param defaultValueSupplier
	 *            the {@link Supplier} of default values
	 */
	public void setDefaultValueSupplier(Supplier<T> defaultValueSupplier) {
		this.defaultValueSupplier = defaultValueSupplier;
	}

	//#end PROPERTY ACCESS

}
