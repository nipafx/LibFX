package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link Property} which is bound to the {@link Nesting#innerObservableProperty() innerObservable} of a
 * {@link Nesting}.
 *
 * @param <T>
 *            the type of the value wrapped by the property which will be build
 */
public final class NestedObjectPropertyBuilder<T>
		extends AbstractNestedPropertyBuilder<Property<T>, NestedProperty<T>> {

	// #region CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedObjectPropertyBuilder(Nesting<Property<T>> nesting) {
		super(nesting);
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
	public NestedObjectProperty<T> build() {
		return new NestedObjectProperty<>(getNesting(), getBean(), getName());
	}

	//#end METHODS

}
