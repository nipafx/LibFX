package org.codefx.libfx.nesting.property;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;

/**
 * Implements the bidirectional binding between a nested property and its nesting's {@link Nesting#innerObservable()
 * innerProperty} and updates the binding when the nesting changes.
 *
 * @param <T>
 *            the type wrapped by the property
 */
class PropertyToNestingBinding<T> {

	// #region PROPERTIES

	/**
	 * The property which will be bound to the {@link #nesting}
	 */
	private final NestedProperty<T> nestedProperty;

	/**
	 * Sets the {@link #nestedProperty}'s {@link NestedProperty#innerObservableNull() innerObservableNull} property.
	 */
	private final Consumer<Boolean> innerObservableNullSetter;

	/**
	 * The nesting to which the {@link #nestedProperty} will be bound.
	 */
	private final Nesting<? extends Property<T>> nesting;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Bidirectionally binds the specified nested property to the specified nesting's property. The specified setter is
	 * used to update the {@code innerObservableNull} property.
	 *
	 * @param nestedProperty
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param innerObservableNullSetter
	 *            the {@link Consumer} which sets the {@link NestedProperty#innerObservableNull()} property
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	private PropertyToNestingBinding(
			NestedProperty<T> nestedProperty, Consumer<Boolean> innerObservableNullSetter,
			Nesting<? extends Property<T>> nesting) {

		Objects.requireNonNull(nestedProperty, "The argument 'property' must not be null.");
		Objects.requireNonNull(innerObservableNullSetter, "The argument 'innerObservableNullSetter' must not be null.");
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		this.nestedProperty = nestedProperty;
		this.innerObservableNullSetter = innerObservableNullSetter;
		this.nesting = nesting;
	}

	/**
	 * Bidirectionally binds the specified nested property to the specified nesting's property. The specified setter is
	 * used to update the {@code innerObservableNull} property.
	 *
	 * @param <T>
	 *            the type wrapped by the property
	 * @param nestedProperty
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param innerObservableNullSetter
	 *            the {@link Consumer} which sets the {@link NestedProperty#innerObservableNull()} property
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	public static <T> void bind(
			NestedProperty<T> nestedProperty, Consumer<Boolean> innerObservableNullSetter,
			Nesting<? extends Property<T>> nesting) {

		PropertyToNestingBinding<T> binding =
				new PropertyToNestingBinding<>(nestedProperty, innerObservableNullSetter, nesting);
		binding.bindToNestingProperty();
	}

	//#end CONSTRUCTION

	// #region BIND

	/**
	 * Binds this property's value to the nesting's property's value and adds a listener which updates that binding.
	 */
	private void bindToNestingProperty() {
		// bind to the nesting's current property
		moveBinding(Optional.empty(), nesting.innerObservable().getValue());
		// add a listener to the nesting which moves the binding from one property to the next
		nesting.innerObservable().addListener(
				(observable, oldProperty, newProperty) -> moveBinding(oldProperty, newProperty));
	}

	/**
	 * Moves the bidirectional binding from the specified old to the specified new observable (one or both can be null).
	 *
	 * @param oldPropertyOpt
	 *            the {@link Property} from which to unbind
	 * @param newPropertyOpt
	 *            the {@link Property} to which to bind
	 */
	private void moveBinding(Optional<? extends Property<T>> oldPropertyOpt,
			Optional<? extends Property<T>> newPropertyOpt) {
		oldPropertyOpt.ifPresent(oldProperty -> nestedProperty.unbindBidirectional(oldProperty));
		newPropertyOpt.ifPresent(newProperty -> nestedProperty.bindBidirectional(newProperty));

		boolean innerObservableNull = !newPropertyOpt.isPresent();
		innerObservableNullSetter.accept(innerObservableNull);
	}

	//#end BIND

}
