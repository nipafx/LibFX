package org.codefx.libfx.nesting.property;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * A {@link LongProperty} which also implements {@link NestedProperty}.
 */
public class NestedLongProperty extends SimpleLongProperty implements NestedProperty<Number> {

	// #region PROPERTIES

	/**
	 * The property indicating whether the nesting's inner observable is currently present, i.e. not null.
	 */
	private final BooleanProperty innerObservablePresent;

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
	NestedLongProperty(Nesting<? extends Property<Number>> nesting, Object bean, String name) {
		super(bean, name);
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		this.innerObservablePresent = new SimpleBooleanProperty(this, "innerObservablePresent");

		PropertyToNestingBinding.bind(this, isPresent -> innerObservablePresent.set(isPresent), nesting);
	}

	//#end CONSTUCTION

	// #region IMPLEMENTATION OF 'NestedProperty'

	@Override
	public ReadOnlyBooleanProperty innerObservablePresent() {
		return innerObservablePresent;
	}

	@Override
	public boolean isInnerObservablePresent() {
		return innerObservablePresent.get();
	}

	//#end IMPLEMENTATION OF 'NestedProperty'

}
