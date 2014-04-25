package org.codefx.nesting.property;

import java.util.Objects;
import java.util.function.Supplier;

import javafx.beans.property.Property;

import org.codefx.nesting.Nesting;

/**
 * Bidirectionally binds a property to the property held by a nesting's {@link Nesting#innerProperty() innerProperty}
 * and updates the binding when the nesting changes.
 * <p>
 * Optionally a default value supplier can be specified which provides values for the case that the property is not
 * bound (can be checked with {@link Property#isBound()}. The property will not be bound if one of the nesting's
 * observables contains null as a value (see {@link Nesting} for a more detailed explanation).
 *
 * @param <T>
 *            the type wrapped by the property
 */
public class PropertyToNestingBinding<T> {

	// #region PROPERTIES

	/**
	 * The property which will be bound to the {@link #nesting}
	 */
	private final Property<T> property;

	/**
	 * The nesting to which the {@link #property} will be bound.
	 */
	private final Nesting<? extends Property<T>> nesting;

	/**
	 * Supplies a default value for the case that the nesting holds no property (i.e. holds null).
	 */
	private final Supplier<T> defaultValueSupplier;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Bidirectionally binds the specified property to the specified nesting's property. In case the latter is null, the
	 * specified supplier is used to create a value for the bound property.
	 *
	 * @param property
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @param defaultValueSupplier
	 *            creates a value for the {@code property} in case the {@code nesting's} property is null
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	private PropertyToNestingBinding(
			Property<T> property, Nesting<? extends Property<T>> nesting, Supplier<T> defaultValueSupplier) {

		Objects.requireNonNull(property, "The argument 'property' must not be null.");
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");
		Objects.requireNonNull(defaultValueSupplier, "The argument 'defaultValueSupplier' must not be null.");

		this.property = property;
		this.nesting = nesting;
		this.defaultValueSupplier = defaultValueSupplier;

		bindToNestingProperty();
	}

	/**
	 * Bidirectionally binds the specified property to the specified nesting's property. In case the latter is null, the
	 * specified supplier is used to create a value for the bound property.
	 *
	 * @param <T>
	 *            the type wrapped by the property
	 * @param property
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @param defaultValueSupplier
	 *            creates a value for the {@code property} in case the {@code nesting's} property is null
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	@SuppressWarnings("unused")
	public static <T> void bind(
			Property<T> property, Nesting<? extends Property<T>> nesting, Supplier<T> defaultValueSupplier) {

		new PropertyToNestingBinding<>(property, nesting, defaultValueSupplier);
	}

	/**
	 * Bidirectionally binds the specified property to the specified nesting's property. In case the latter is null, the
	 * bound property's value will be set to the specified defaultValue.
	 *
	 * @param <T>
	 *            the type wrapped by the property
	 * @param property
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @param defaultValue
	 *            the value for the {@code property} in case the {@code nesting's} property is null
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	public static <T> void bind(
			Property<T> property, Nesting<? extends Property<T>> nesting, T defaultValue) {

		bind(property, nesting, () -> defaultValue);
	}

	/**
	 * Bidirectionally binds the specified property to the specified nesting's property. In case the latter is null, the
	 * bound property's value will be set to null.
	 *
	 * @param <T>
	 *            the type wrapped by the property
	 * @param property
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	public static <T> void bind(Property<T> property, Nesting<? extends Property<T>> nesting) {
		bind(property, nesting, (T) null);
	}

	//#end CONSTRUCTION

	/**
	 * Binds this property's value to the nesting's property's value and adds a listener which updates that binding.
	 */
	private void bindToNestingProperty() {
		// bind to the nesting's current property
		moveBinding(null, nesting.innerProperty().getValue());
		// add a listener to the nesting which moves the binding from one property to the next
		nesting.innerProperty().addListener(
				(observable, oldProperty, newProperty) -> moveBinding(oldProperty, newProperty));
	}

	/**
	 * Moves the bidirectional binding from the specified old to the specified new observable (one or both can be null).
	 *
	 * @param oldProperty
	 *            the {@link Property} from which to unbind
	 * @param newProperty
	 *            the {@link Property} to which to bind
	 */
	private void moveBinding(Property<T> oldProperty, Property<T> newProperty) {
		// unbind from the old property
		if (oldProperty != null)
			property.unbindBidirectional(oldProperty);

		// bind to the new property if it exists; otherwise set null as this property's new value
		if (newProperty == null) {
			T defaultValue = defaultValueSupplier.get();
			property.setValue(defaultValue);
		} else
			property.bindBidirectional(newProperty);
	}

}
