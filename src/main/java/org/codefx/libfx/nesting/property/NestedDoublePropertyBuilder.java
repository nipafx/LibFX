package org.codefx.libfx.nesting.property;

import javafx.beans.property.DoubleProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedDoubleProperty} which is bound to the {@link Nesting#innerObservableProperty()
 * innerObservable} of a {@link Nesting}.
 */
public class NestedDoublePropertyBuilder extends
		AbstractNestedPropertyBuilder<Double, DoubleProperty, NestedDoubleProperty, NestedDoublePropertyBuilder> {

	// #begin CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedDoublePropertyBuilder(Nesting<DoubleProperty> nesting) {
		super(nesting);
	}

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 * @return a new instance of {@link NestedDoublePropertyBuilder}
	 */
	public static NestedDoublePropertyBuilder forNesting(Nesting<DoubleProperty> nesting) {
		return new NestedDoublePropertyBuilder(nesting);
	}

	//#end CONSTRUCTION

	// #begin METHODS

	@Override
	public NestedDoubleProperty build() {
		return new NestedDoubleProperty(getNesting(), getInnerObservableMissingBehavior(), getBean(), getName());
	}

	//#end METHODS

}
