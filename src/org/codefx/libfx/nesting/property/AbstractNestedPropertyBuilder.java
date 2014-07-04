package org.codefx.libfx.nesting.property;

import java.util.Objects;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * Abstract superclass to nested property builders. Collects common builder settings; e.g. for the new property's
 * {@link Property#getBean() bean} and {@link Property#getName() name}.
 *
 * @param <N>
 *            the nesting hierarchy's innermost type of {@link Property}
 * @param <P>
 *            the type of {@link Property} which will be built
 */
abstract class AbstractNestedPropertyBuilder<N extends Property<?>, P extends NestedProperty<?>> {

	// #region PROPERTIES

	/**
	 * The nesting which will be used for all nested properties.
	 */
	private final Nesting<N> nesting;

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
	protected AbstractNestedPropertyBuilder(Nesting<N> nesting) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		this.nesting = nesting;
	}

	//#end CONSTRUCTOR

	// #region ABSTRACT METHODS

	/**
	 * Creates a new property instance. This method can be called arbitrarily often and each call returns a new
	 * instance.
	 *
	 * @return the new instance of {@code P}, i.e. an implementation of {@link NestedProperty}
	 */
	public abstract P build();

	//#end ABSTRACT METHODS

	// #region PROPERTY ACCESS

	/**
	 * @return the nesting which will be used for all nested properties
	 */
	protected final Nesting<N> getNesting() {
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
	protected final void setTheBean(Object bean) {
		Objects.requireNonNull(bean, "The argument 'bean' must not be null.");
		this.bean = bean;
	}

	/**
	 * Sets the property's future {@link Property#getBean() bean}.
	 *
	 * @param bean
	 *            the property's future bean
	 * @return this builder
	 */
	public AbstractNestedPropertyBuilder<N, P> setBean(Object bean) {
		Objects.requireNonNull(bean, "The argument 'bean' must not be null.");
		this.bean = bean;
		return this;
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
	protected final void setTheName(String name) {
		Objects.requireNonNull(name, "The argument 'name' must not be null.");
		this.name = name;
	}

	/**
	 * Sets the property's future {@link Property#getName() name}.
	 *
	 * @param name
	 *            the property's future name
	 * @return this builder
	 */
	public AbstractNestedPropertyBuilder<N, P> setName(String name) {
		setTheName(name);
		return this;
	}

	//#end PROPERTY ACCESS

}
