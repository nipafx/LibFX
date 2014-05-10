package org.codefx.nesting.property;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.Nesting;

/**
 * An {@link ObjectProperty} which also implements {@link NestedProperty}.
 *
 * @param <T>
 *            the type of the value wrapped by this property
 */
public class NestedObjectProperty<T> extends SimpleObjectProperty<T> implements NestedProperty<T> {

	// #region PROPERTIES

	/**
	 * The property indicating whether the nesting's inner observable is currently null.
	 */
	private final BooleanProperty innerObservableNull;

	//#end PROPERTIES

	// #region CONSTUCTION

	/**
	 * Creates a new property. Except {@code nesting} all arguments can be null.
	 *
	 * @param nesting
	 *            the nesting this property is based on
	 * @param bean
	 *            the bean which owns this property; can be null
	 * @param name
	 *            this property's name; can be null
	 */
	NestedObjectProperty(Nesting<? extends Property<T>> nesting, Object bean, String name) {
		super(bean, name);
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		this.innerObservableNull = new SimpleBooleanProperty(this, "innerObservableNull");

		PropertyToNestingBinding.bind(this, isNull -> innerObservableNull.set(isNull), nesting);
	}

	//#end CONSTUCTION

	// #region IMPLEMENTATION OF 'NestedProperty'

	@Override
	public ReadOnlyBooleanProperty innerObservableNull() {
		return innerObservableNull;
	}

	@Override
	public boolean isInnerObservableNull() {
		return innerObservableNull.get();
	}

	//#end IMPLEMENTATION OF 'NestedProperty'

}
