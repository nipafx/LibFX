package org.codefx.libfx.nesting.property;

import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedStringProperty} which is bound to the {@link Nesting#innerObservableProperty() innerObservable}
 * of a {@link Nesting}.
 */
public class NestedStringPropertyBuilder extends AbstractNestedPropertyBuilder<StringProperty, NestedStringProperty> {

	// #region CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedStringPropertyBuilder(Nesting<StringProperty> nesting) {
		super(nesting);
	}

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 * @return a new instance of {@link NestedStringPropertyBuilder}
	 */
	public static NestedStringPropertyBuilder forNesting(Nesting<StringProperty> nesting) {
		return new NestedStringPropertyBuilder(nesting);
	}

	//#end CONSTRUCTION

	// #region METHODS

	@Override
	public NestedStringProperty build() {
		return new NestedStringProperty(getNesting(), getBean(), getName());
	}

	//#end METHODS

}
