package org.codefx.libfx.nesting.property;

import javafx.beans.property.IntegerProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedIntegerProperty} which is bound to the {@link Nesting#innerObservableProperty()
 * innerObservable} of a {@link Nesting}.
 */
public class NestedIntegerPropertyBuilder extends
		AbstractNestedPropertyBuilder<Integer, IntegerProperty, NestedIntegerProperty, NestedIntegerPropertyBuilder> {

	// #begin CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedIntegerPropertyBuilder(Nesting<IntegerProperty> nesting) {
		super(nesting);
	}

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 * @return a new instance of {@link NestedIntegerPropertyBuilder}
	 */
	public static NestedIntegerPropertyBuilder forNesting(Nesting<IntegerProperty> nesting) {
		return new NestedIntegerPropertyBuilder(nesting);
	}

	//#end CONSTRUCTION

	// #begin METHODS

	@Override
	public NestedIntegerProperty build() {
		return new NestedIntegerProperty(getNesting(), getInnerObservableMissingBehavior(), getBean(), getName());
	}

	//#end METHODS

}
