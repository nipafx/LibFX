package org.codefx.libfx.nesting.property;

import javafx.beans.property.FloatProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedFloatProperty} which is bound to the {@link Nesting#innerObservableProperty() innerObservable}
 * of a {@link Nesting}.
 */
public class NestedFloatPropertyBuilder extends AbstractNestedPropertyBuilder<FloatProperty, NestedFloatProperty> {

	// #region CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedFloatPropertyBuilder(Nesting<FloatProperty> nesting) {
		super(nesting);
	}

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 * @return a new instance of {@link NestedFloatPropertyBuilder}
	 */
	public static NestedFloatPropertyBuilder forNesting(Nesting<FloatProperty> nesting) {
		return new NestedFloatPropertyBuilder(nesting);
	}

	//#end CONSTRUCTION

	// #region METHODS

	@Override
	public NestedFloatProperty build() {
		return new NestedFloatProperty(getNesting(), getBean(), getName());
	}

	//#end METHODS

}
