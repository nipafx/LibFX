package org.codefx.nesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import org.codefx.nesting.property.NestedObjectProperty;
import org.codefx.nesting.property.NestedObjectPropertyBuilder;

/**
 * A builder for all kinds of nested functionality.
 * <p>
 * TODO: examples
 *
 * @param <T>
 *            the type of the wrapped value
 */
public class NestingBuilder<T> {

	// #region PROPERTIES

	// for nesting

	/**
	 * Indicates whether this is the outer builder, i.e. the one whose {@link #outerObservable} is not null.
	 */
	private final boolean isOuterBuilder;

	/**
	 * The outer observable value upon all nestings depend. This is only non-null for the outer builder. All others have
	 * a {@link #previousNestedBuilder} and a {@link #nestedObservableGetter}.
	 */
	private final ObservableValue<T> outerObservable;

	/**
	 * The previous builder upon which this builder depends. This is only non-null for nested builders.
	 */
	private final NestingBuilder<?> previousNestedBuilder;

	/**
	 * The function which performs the "nesting step" from an instance of the previous builder's wrapped type to the
	 * next observable.
	 */
	private final Function<?, ? extends ObservableValue<T>> nestedObservableGetter;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder, i.e. has the specified {@link #outerObservable}
	 * and {@link #isOuterBuilder} set to true.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	private NestingBuilder(ObservableValue<T> outerObservable) {
		this.isOuterBuilder = true;
		this.outerObservable = outerObservable;
		this.previousNestedBuilder = null;
		this.nestedObservableGetter = null;
	}

	/**
	 * Creates a new nesting builder which acts as an inner builder, i.e. has the specified
	 * {@link #previousNestedBuilder} and {@link #nestedObservableGetter} as its {@link #isOuterBuilder} set to false.
	 *
	 * @param <O>
	 *            the type the previous builder wraps
	 * @param previousNestedBuilder
	 *            the previous builder
	 * @param nestedObservableGetter
	 *            the function which performs the "nesting step" from one observable to the next; it does so by getting
	 *            the nested observable value from an instance of the previous builder's wrapped type
	 */
	private <O> NestingBuilder(NestingBuilder<O> previousNestedBuilder,
			Function<O, ? extends ObservableValue<T>> nestedObservableGetter) {
		this.isOuterBuilder = false;
		this.outerObservable = null;
		this.previousNestedBuilder = previousNestedBuilder;
		this.nestedObservableGetter = nestedObservableGetter;
	}

	/**
	 * Returns a builder for nestings which depend on the specified outer observable value.
	 *
	 * @param <T>
	 *            the type wrapped by the outer observable value as well as of the created nestings
	 * @param outerObservableValue
	 *            the outer observable value upon which all nestings build by the returned builder depend
	 * @return a {@link NestingBuilder} which builds a nesting depending on the specified outer observable value
	 * @throws NullPointerException
	 *             if the specified observable value is null
	 */
	public static <T> NestingBuilder<T> on(ObservableValue<T> outerObservableValue) {
		Objects.requireNonNull(outerObservableValue, "The argument 'outerObservableValue' must not be null.");
		return new NestingBuilder<>(outerObservableValue);
	}

	/**
	 * Returns a builder for nestings which depend on this builder's outer property and "nesting steps" and adds the
	 * specified nested observable value getter as the next step.
	 *
	 * @param <S>
	 *            the type wrapped by the created nestings
	 * @param nestedObservableValueGetter
	 *            the function which performs the "nesting step" from one observable to the next; it does so by getting
	 *            the nested observable value from an instance of this builder's wrapped type
	 * @return a {@link NestingBuilder} which builds a nesting depending on the specified outer observable value
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public <S> NestingBuilder<S> nest(Function<T, ? extends ObservableValue<S>> nestedObservableValueGetter) {
		Objects.requireNonNull(nestedObservableValueGetter,
				"The argument 'nestedObservableValueGetter' must not be null.");
		return new NestingBuilder<S>(this, nestedObservableValueGetter);
	}

	//#end CONSTRUCTION

	// #region BUILD

	/**
	 * Fills the specified kit with an observable value and all observable getters which were given to this and its
	 * previous nesting builders.
	 *
	 * @param kit
	 *            the {@link NestingConstructionKit} to fill with an {@link #outerObservable} and
	 *            {@link #nestedObservableGetter nestedObservableGetters}
	 */
	private void fillNestingConstructionKit(NestingConstructionKit kit) {

		/*
		 * Uses recursion to move up the chain of 'previousNestedBuilder's until the outer builder is reached. This
		 * builder's 'outerObservable' is set to the specified 'kit'. The 'kit's list of observable getters is then
		 * filled in closing the recursion, i.e. top down from the outermost builder to the inner ones. This creates the
		 * list on the correct order for the 'Nesting' constructor.
		 */

		if (isOuterBuilder)
			kit.setOuterObservable(outerObservable);
		else {
			previousNestedBuilder.fillNestingConstructionKit(kit);
			kit.getNestedObservableGetters().add(nestedObservableGetter);
		}
	}

	/**
	 * Creates a new nesting from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new instance of {@link DeepNesting}
	 */
	public Nesting<Property<T>> buildNesting() {
		if (isOuterBuilder)
			// TODO: implement
			throw new RuntimeException("Not yet implemented.");

		// create and fill a construction kit
		NestingConstructionKit kit = new NestingConstructionKit();
		fillNestingConstructionKit(kit);
		// use its content to create the nesting
		return new DeepNesting<>(kit.getOuterObservable(), kit.getNestedObservableGetters());
	}

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new instance of {@link NestedObjectProperty} with no owning bean, no name and no default value
	 */
	public Property<T> buildProperty() {
		Nesting<Property<T>> nesting = buildNesting();
		return NestedObjectPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedObjectPropertyBuilder}
	 */
	public NestedObjectPropertyBuilder<T> buildPropertyWithBuilder() {
		Nesting<Property<T>> nesting = buildNesting();
		return NestedObjectPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD

	// #region PRIVATE CLASSES

	/**
	 * An editable class which can be used to collect all instances needed to call
	 * {@link DeepNesting#DeepNesting(ObservableValue, List) new Nesting(...)}.
	 */
	@SuppressWarnings("rawtypes")
	private static class NestingConstructionKit {

		// #region PROPERTIES

		/**
		 * The outer {@link ObservableValue}
		 */
		private ObservableValue outerObservable;

		/**
		 * The list of {@link Function Functions} which get the nested {@link ObservableValue ObservableValues}.
		 */
		private final List<Function> nestedObservableGetters;

		//#end PROPERTIES

		// #region CONSTRUCTOR

		/**
		 * Creates a new empty construction kit.
		 */
		public NestingConstructionKit() {
			nestedObservableGetters = new ArrayList<>();
		}

		//#end CONSTRUCTOR

		// #region PROPERTY ACCESS

		/**
		 * @return the outer {@link ObservableValue}
		 */
		public ObservableValue getOuterObservable() {
			return outerObservable;
		}

		/**
		 * Sets the new outer observable value.
		 *
		 * @param outerObservable
		 *            the outer {@link ObservableValue} to set
		 */
		public void setOuterObservable(ObservableValue outerObservable) {
			this.outerObservable = outerObservable;
		}

		/**
		 * @return the list of {@link Function Functions} which get the nested {@link ObservableValue ObservableValues}
		 */
		public List<Function> getNestedObservableGetters() {
			return nestedObservableGetters;
		}

		//#end PROPERTY ACCESS

	}

	//#end PRIVATE CLASSES

}
