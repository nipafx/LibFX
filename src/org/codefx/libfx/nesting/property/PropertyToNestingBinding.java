package org.codefx.libfx.nesting.property;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.NestingObserver;

/**
 * Implements the bidirectional binding between a nested property and its nesting's
 * {@link Nesting#innerObservableProperty() innerObservable} and updates the binding when the nesting changes.
 *
 * @param <T>
 *            the type wrapped by the property
 */
class PropertyToNestingBinding<T> {

	/**
	 * Bidirectionally binds the specified nested property to the specified nesting's property. The specified setter is
	 * used to update the nested property's {@link NestedProperty#innerObservablePresentProperty()
	 * innerObservablePresent} property.
	 *
	 * @param <T>
	 *            the type wrapped by the property
	 * @param nestedProperty
	 *            the {@link Property} which will be bound to the specified nesting
	 * @param innerObservablePresentSetter
	 *            the {@link Consumer} which sets the {@link NestedProperty#innerObservablePresentProperty()} property
	 * @param nesting
	 *            the {@link Nesting} to which the property will be bound
	 * @throws NullPointerException
	 *             if any of the arguments is null
	 */
	public static <T> void bind(
			NestedProperty<T> nestedProperty, Consumer<Boolean> innerObservablePresentSetter,
			Nesting<? extends Property<T>> nesting) {

		Objects.requireNonNull(nestedProperty, "The argument 'property' must not be null.");
		Objects.requireNonNull(innerObservablePresentSetter,
				"The argument 'innerObservablePresentSetter' must not be null.");
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		// the 'innerObservablePresentSetter' only accepts one Boolean; create a 'BiConsumer' from it,
		// which accepts two and ignores the first
		BiConsumer<Boolean, Boolean> innerObservablePresentBiSetter =
				(oldPropertyPresent, newPropertyPresent) -> innerObservablePresentSetter.accept(newPropertyPresent);

				// use a nesting observer to accomplish the binding/unbinding
		NestingObserver
				.observe(nesting)
				.withOldInnerObservable(oldProperty -> nestedProperty.unbindBidirectional(oldProperty))
				.withNewInnerObservable(newProperty -> nestedProperty.bindBidirectional(newProperty))
				.whenInnerObservableChanges(innerObservablePresentBiSetter)
				.build();
	}

}
