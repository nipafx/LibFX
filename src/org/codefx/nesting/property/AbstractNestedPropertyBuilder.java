package org.codefx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.nesting.Nesting;

/**
 * Abstract superclass to nested property builders. Collects common builder settings; e.g. for the new property's
 * {@link Property#getBean() bean} and {@link Property#getName() name}.
 *
 * @param <P>
 *            the nesting hierarchy's innermost type of {@link Property}
 */
abstract class AbstractNestedPropertyBuilder<P extends Property<?>> {

	// #region PROPERTIES

	/**
	 * The nesting which will be used for all nested properties.
	 */
	private final Nesting<P> nesting;

	/**
	 * The property's future {@link Property#getBean() bean}.
	 */
	private Object bean;

	/**
	 * The property's future {@link Property#getName() name}.
	 */
	private String name;

	//#end PROPERTIES

	// #region CONSTRUCTOR

	/**
	 * Creates a new abstract builder which uses the specified nesting.
	 *
	 * @param nesting
	 *            the nesting which will be used for all nested properties
	 */
	protected AbstractNestedPropertyBuilder(Nesting<P> nesting) {
		this.nesting = nesting;
	}

	//#end CONSTRUCTOR

	// #region ABSTRACT METHODS

	/**
	 * Creates a new property instance. This method can be called arbitrarily often and each call returns a new
	 * instance.
	 *
	 * @return the new instance of {@code P}, i.e. an implementation of {@link Property}
	 */
	public abstract P build();

	//#end ABSTRACT METHODS

	// #region PROPERTY ACCESS

	/**
	 * @return the nesting which will be used for all nested properties
	 */
	protected final Nesting<P> getNesting() {
		return nesting;
	}

	/**
	 * @return the property's future {@link Property#getBean() bean}.
	 */
	protected final Object getBean() {
		return bean;
	}

	/**
	 * Sets the property's future {@link Property#getBean() bean}.
	 *
	 * @param bean
	 *            the property's future bean
	 */
	public final void setBean(Object bean) {
		this.bean = bean;
	}

	/**
	 * @return the property's future {@link Property#getBean() bean}.
	 */
	protected final String getName() {
		return name;
	}

	/**
	 * Sets the property's future {@link Property#getName() name}.
	 *
	 * @param name
	 *            the property's future name
	 */
	public final void setName(String name) {
		this.name = name;
	}

	//#end PROPERTY ACCESS

}
