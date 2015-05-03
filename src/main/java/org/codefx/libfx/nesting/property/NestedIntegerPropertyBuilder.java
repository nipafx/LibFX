package org.codefx.libfx.nesting.property;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedIntegerProperty} which is bound to the {@link Nesting#innerObservableProperty()
 * innerObservable} of a {@link Nesting}.
 */
public class NestedIntegerPropertyBuilder extends AbstractNestedPropertyBuilder<IntegerProperty, NestedIntegerProperty> {

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
		return new NestedIntegerProperty(getNesting(), getBean(), getName());
	}

	/**
	 * Sets the property's future {@link Property#getBean() bean}.
	 *
	 * @param bean
	 *            the property's future bean
	 * @return this builder
	 */
	@Override
	public NestedIntegerPropertyBuilder setBean(Object bean) {
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
	public NestedIntegerPropertyBuilder setName(String name) {
		setTheName(name);
		return this;
	}

	//#end METHODS

}
