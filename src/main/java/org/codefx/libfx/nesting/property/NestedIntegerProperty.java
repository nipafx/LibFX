package org.codefx.libfx.nesting.property;

import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * An {@link IntegerProperty} which also implements {@link NestedProperty}.
 */
public class NestedIntegerProperty extends SimpleIntegerProperty implements NestedProperty<Number> {

	// #begin PROPERTIES

	/**
	 * The property indicating whether the nesting's inner observable is currently present, i.e. not null.
	 */
	private final BooleanProperty innerObservablePresent;

	//#end PROPERTIES

	// #begin CONSTUCTION

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
	NestedIntegerProperty(Nesting<? extends Property<Number>> nesting, Object bean, String name) {
		super(bean, name);
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		this.innerObservablePresent = new SimpleBooleanProperty(this, "innerObservablePresent");

		PropertyToNestingBinding.bind(this, isPresent -> innerObservablePresent.set(isPresent), nesting);
	}

	//#end CONSTUCTION

	// #begin IMPLEMENTATION OF 'NestedProperty'

	@Override
	public ReadOnlyBooleanProperty innerObservablePresentProperty() {
		return innerObservablePresent;
	}

	@Override
	public boolean isInnerObservablePresent() {
		return innerObservablePresent.get();
	}

	//#end IMPLEMENTATION OF 'NestedProperty'

}
