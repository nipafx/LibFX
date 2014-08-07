package org.codefx.libfx.nesting.property;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * A builder for a {@link NestedDoubleProperty} which is bound to the {@link Nesting#innerObservableProperty()
 * innerObservable} of a {@link Nesting}.
 */
public class NestedDoublePropertyBuilder extends AbstractNestedPropertyBuilder<DoubleProperty, NestedDoubleProperty> {

	// #region CONSTRUCTION

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

	// #region METHODS

	@Override
	public NestedDoubleProperty build() {
		return new NestedDoubleProperty(getNesting(), getBean(), getName());
	}

	/**
	 * Sets the property's future {@link Property#getBean() bean}.
	 *
	 * @param bean
	 *            the property's future bean
	 * @return this builder
	 */
	@Override
	public NestedDoublePropertyBuilder setBean(Object bean) {
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
	public NestedDoublePropertyBuilder setName(String name) {
		setTheName(name);
		return this;
	}

	//#end METHODS

}
