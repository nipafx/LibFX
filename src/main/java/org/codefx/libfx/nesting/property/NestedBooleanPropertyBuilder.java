package org.codefx.libfx.nesting.property;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedBooleanProperty} which is bound to the {@link Nesting#innerObservableProperty()
 * innerObservable} of a {@link Nesting}.
 */
public class NestedBooleanPropertyBuilder extends AbstractNestedPropertyBuilder<BooleanProperty, NestedBooleanProperty> {

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
		return new NestedBooleanProperty(getNesting(), getBean(), getName());
	}

	/**
	 * Sets the property's future {@link Property#getBean() bean}.
	 *
	 * @param bean
	 *            the property's future bean
	 * @return this builder
	 */
	@Override
	public NestedBooleanPropertyBuilder setBean(Object bean) {
		setTheBean(bean);
		return this;
	}

	/**
	 * Sets the property's future {@link Property#getName() name}.
	 *
	 * @param name
	 *            the property's future name
	 * @return this builder
	 */
	@Override
	public NestedBooleanPropertyBuilder setName(String name) {
		setTheName(name);
		return this;
	}

	//#end METHODS

}
