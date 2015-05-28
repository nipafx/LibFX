package org.codefx.libfx.nesting.property;

import javafx.beans.property.BooleanProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedBooleanProperty} which is bound to the {@link Nesting#innerObservableProperty()
 * innerObservable} of a {@link Nesting}.
 */
public class NestedBooleanPropertyBuilder extends
		AbstractNestedPropertyBuilder<Boolean, BooleanProperty, NestedBooleanProperty, NestedBooleanPropertyBuilder> {

	// #begin CONSTRUCTION

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	private NestedBooleanPropertyBuilder(Nesting<BooleanProperty> nesting) {
		super(nesting);
	}

	/**
	 * Creates a new builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 * @return a new instance of {@link NestedBooleanPropertyBuilder}
	 */
	public static NestedBooleanPropertyBuilder forNesting(Nesting<BooleanProperty> nesting) {
		return new NestedBooleanPropertyBuilder(nesting);
	}

	//#end CONSTRUCTION

	// #begin METHODS

	@Override
	public NestedBooleanProperty build() {
		return new NestedBooleanProperty(getNesting(), getInnerObservableMissingBehavior(), getBean(), getName());
	}

	//#end METHODS

}
