package org.codefx.nesting.testhelper;

import java.util.Objects;
import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import org.codefx.nesting.Nesting;

/**
 * Provides simple usability functions to access an outer observables nesting hierarchy in a more readable way. To that
 * end many arguments and return types can be null (see method comments).
 */
public class NestingAccess {

	// #region NESTING

	/**
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable will be returned
	 * @return {@link Nesting#innerObservable()}.{@link ReadOnlyProperty#getValue() getValue()}.{@link Optional#get()};
	 *         can be null
	 */
	public static <O extends Observable> O getNestingObservable(Nesting<O> nesting) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		Optional<O> nestingObservable = nesting.innerObservable().getValue();
		if (nestingObservable.isPresent())
			return nestingObservable.get();
		else
			return null;
	}

	/**
	 * Sets the specified nesting's observable value to the specified new observable.
	 *
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable will be set
	 * @param newObservable
	 *            the nesting's new observable; can be null
	 */
	public static <O extends Observable> void setNestingObservable(EditableNesting<O> nesting, O newObservable) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		nesting.setInnerObservable(Optional.ofNullable(newObservable));
	}

	/**
	 * @param <T>
	 *            the type of the nesting's observable's value
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable's value will be returned
	 * @return the value of the observable in {@link Nesting#innerObservable()}; can be null
	 * @throws NullPointerException
	 *             if the nesting's inner observable is null
	 */
	public static <T, O extends ObservableValue<T>> T getNestingValue(Nesting<O> nesting) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		return getNestingObservable(nesting).getValue();
	}

	/**
	 * Sets the specified nesting's observable's value to the specified new value.
	 *
	 * @param <T>
	 *            the type of the nesting's observable's value
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable's value will be set
	 * @param newValue
	 *            the nesting's observable's new value; can be null.
	 * @throws NullPointerException
	 *             if the nesting's inner observable is null
	 */
	public static <T, O extends Property<T>> void setNestingValue(Nesting<O> nesting, T newValue) {
		Objects.requireNonNull(nesting, "The argument 'nesting' must not be null.");

		getNestingObservable(nesting).setValue(newValue);
	}

	//#end NESTING

	// #region NESTED HIERARCHY

	/**
	 * @param outerObservable
	 *            the outer observable whose outer value will be returned
	 * @return outerObservable -> outerValue; can be null
	 */
	public static OuterValue getOuterValue(ObservableValue<OuterValue> outerObservable) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		return outerObservable.getValue();
	}

	/**
	 * Sets a new outer value.
	 *
	 * @param outerObservable
	 *            the outer observable whose outer value will be set
	 * @param outerValue
	 *            the new outer value; can be null
	 */
	public static void setOuterValue(Property<OuterValue> outerObservable, OuterValue outerValue) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		outerObservable.setValue(outerValue);
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner value will be returned
	 * @return outerObservable -> outerValue -> innerValue; can be null
	 * @throws NullPointerException
	 *             if the outer observable's value is null
	 */
	public static InnerValue getInnerValue(ObservableValue<OuterValue> outerObservable) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		return outerObservable.getValue().getInnerValue();
	}

	/**
	 * Sets a new inner value.
	 *
	 * @param outerObservable
	 *            the outer observable whose inner value will be set
	 * @param innerValue
	 *            the new inner value
	 * @throws NullPointerException
	 *             if the outer observable's value is null
	 */
	public static void setInnerValue(ObservableValue<OuterValue> outerObservable, InnerValue innerValue) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		outerObservable.getValue().setInnerValue(innerValue);
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner observable will be returned
	 * @return outerObservable -> outerType -> innerType -> observable
	 * @throws NullPointerException
	 *             if the outer observable's value or the inner value is null
	 */
	public static Observable getInnerObservable(ObservableValue<OuterValue> outerObservable) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		return getInnerValue(outerObservable).observable();
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner property will be returned
	 * @return outerObservable -> outerType -> innerType -> property
	 * @throws NullPointerException
	 *             if the outer observable's value or the inner value is null
	 */
	public static Property<SomeValue> getInnerProperty(ObservableValue<OuterValue> outerObservable) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		return getInnerValue(outerObservable).property();
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner integer property will be returned
	 * @return outerObservable -> outerType -> innerType -> integerProperty
	 * @throws NullPointerException
	 *             if the outer observable's value or the inner value is null
	 */
	public static IntegerProperty getInnerIntegerProperty(ObservableValue<OuterValue> outerObservable) {
		return getInnerValue(outerObservable).integerProperty();
	}

	//#end NESTED HIERARCHY

	// #region INNER CLASSES

	/**
	 * An implementation of {@link Nesting} which does no real nesting. Instead it allows to directly edit the
	 * {@link Nesting#innerObservable()}.
	 *
	 * @param <O>
	 *            the type of the inner observable
	 */
	public static class EditableNesting<O extends Observable> implements Nesting<O> {

		/**
		 * The property holding the inner observable.
		 */
		private final Property<Optional<O>> innerObservable;

		/**
		 * Creates a new editable nesting.
		 */
		private EditableNesting() {
			this.innerObservable = new SimpleObjectProperty<Optional<O>>(this, "innerObservable", Optional.empty());
		}

		/**
		 * Creates a new editable nesting with the specified inner observable.
		 *
		 * @param <O>
		 *            the type of the inner observable
		 * @param innerObservable
		 *            the inner observable of the returned nesting
		 * @return a new editable nesting instance
		 */
		public static <O extends Observable> EditableNesting<O> createWithInnerObservable(O innerObservable) {
			Objects.requireNonNull(innerObservable, "The argument 'innerObservable' must not be null.");

			EditableNesting<O> nesting = new EditableNesting<>();
			Optional<O> innerObservableOptional = Optional.of(innerObservable);
			nesting.setInnerObservable(innerObservableOptional);
			return nesting;
		}

		/**
		 * Creates a new editable nesting with null as its inner observable.
		 *
		 * @param <O>
		 *            the type of the inner observable
		 * @return a new editable nesting instance
		 */
		public static <O extends Observable> EditableNesting<O> createWithInnerObservableNull() {
			return new EditableNesting<>();
		}

		/**
		 * The property holding the inner observable.
		 *
		 * @return the innerObservable as a property
		 */
		@Override
		public Property<Optional<O>> innerObservable() {
			return innerObservable;
		}

		/**
		 * The property holding the inner observable.
		 *
		 * @return the innerObservable
		 */
		public Optional<O> getInnerObservable() {
			return innerObservable().getValue();
		}

		/**
		 * The property holding the inner observable.
		 *
		 * @param innerObservable
		 *            the innerObservable to set
		 */
		public void setInnerObservable(Optional<O> innerObservable) {
			innerObservable().setValue(innerObservable);
		}

	}

	//#end INNER CLASSES

}
