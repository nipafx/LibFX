package org.codefx.nesting.testhelper;

import java.util.Objects;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import org.codefx.nesting.Nesting;

/**
 * Provides simple usability functions to access an outer observables nesting hierarchy in a more readable way.
 */
public class NestingAccess {

	// #region NESTING

	/**
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable will be returned
	 * @return {@link Nesting#innerObservable()}.{@link ReadOnlyProperty#getValue() getValue()}
	 */
	public static <O extends Observable> O getNestingObservable(Nesting<O> nesting) {
		return nesting.innerObservable().getValue();
	}

	/**
	 * Sets the specified nesting's observable value to the specified new observable.
	 *
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable will be set
	 * @param newObservable
	 *            the nesting's new observable
	 */
	public static <O extends Observable> void setNestingObservable(EditableNesting<O> nesting, O newObservable) {
		nesting.setInnerObservable(newObservable);
	}

	/**
	 * @param <T>
	 *            the type of the nesting's observable's value
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable's value will be returned
	 * @return {@link Nesting#innerObservable()}.{@link ReadOnlyProperty#getValue() getValue()}.
	 *         {@link ObservableValue#getValue() getValue()}
	 */
	public static <T, O extends ObservableValue<T>> T getNestingValue(Nesting<O> nesting) {
		return getNestingObservable(nesting).getValue();
	}

	/**
	 * Sets the specified nesting's observable's value to the specified new value by calling
	 * {@link Nesting#innerObservable()}.{@link ReadOnlyProperty#getValue() getValue()}.
	 * {@link Property#setValue(Object) setValue(newValue)}.
	 *
	 * @param <T>
	 *            the type of the nesting's observable's value
	 * @param <O>
	 *            the type of observable the nesting contains
	 * @param nesting
	 *            the nesting whose observable's value will be set
	 * @param newValue
	 *            the nesting's observable's new value
	 */
	public static <T, O extends Property<T>> void setNestingValue(Nesting<O> nesting, T newValue) {
		getNestingObservable(nesting).setValue(newValue);
	}

	//#end NESTING

	// #region NESTED HIERARCHY

	/**
	 * @param outerObservable
	 *            the outer observable whose outer value will be returned
	 * @return outerObservable -> outerValue
	 */
	public static OuterValue getOuterValue(ObservableValue<OuterValue> outerObservable) {
		return outerObservable.getValue();
	}

	/**
	 * Sets a new outer value.
	 *
	 * @param outerObservable
	 *            the outer observable whose outer value will be set
	 * @param outerValue
	 *            the new outer value
	 */
	public static void setOuterValue(Property<OuterValue> outerObservable, OuterValue outerValue) {
		outerObservable.setValue(outerValue);
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner value will be returned
	 * @return outerObservable -> outerValue -> innerValue
	 */
	public static InnerValue getInnerValue(ObservableValue<OuterValue> outerObservable) {
		return outerObservable.getValue().getInnerValue();
	}

	/**
	 * Sets a new inner value.
	 *
	 * @param outerObservable
	 *            the outer observable whose inner value will be set
	 * @param innerValue
	 *            the new inner value
	 */
	public static void setInnerValue(ObservableValue<OuterValue> outerObservable, InnerValue innerValue) {
		outerObservable.getValue().setInnerValue(innerValue);
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner observable will be returned
	 * @return outerObservable -> outerType -> innerType -> observable
	 */
	public static Observable getInnerObservable(ObservableValue<OuterValue> outerObservable) {
		return getInnerValue(outerObservable).observable();
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner property will be returned
	 * @return outerObservable -> outerType -> innerType -> property
	 */
	public static Property<SomeValue> getInnerProperty(ObservableValue<OuterValue> outerObservable) {
		return getInnerValue(outerObservable).property();
	}

	/**
	 * @param outerObservable
	 *            the outer observable whose inner integer property will be returned
	 * @return outerObservable -> outerType -> innerType -> integerProperty
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
		private final Property<O> innerObservable;

		/**
		 * Creates a new editable nesting.
		 */
		private EditableNesting() {
			this.innerObservable = new SimpleObjectProperty<O>(this, "innerObservable");
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
			nesting.setInnerObservable(innerObservable);
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
		public Property<O> innerObservable() {
			return innerObservable;
		}

		/**
		 * The property holding the inner observable.
		 *
		 * @return the innerObservable
		 */
		public O getInnerObservable() {
			return innerObservable().getValue();
		}

		/**
		 * The property holding the inner observable.
		 *
		 * @param innerObservable
		 *            the innerObservable to set
		 */
		public void setInnerObservable(O innerObservable) {
			innerObservable().setValue(innerObservable);
		}

	}

	//#end INNER CLASSES

}
