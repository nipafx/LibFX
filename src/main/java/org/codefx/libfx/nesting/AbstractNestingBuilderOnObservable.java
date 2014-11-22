package org.codefx.libfx.nesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.listener.NestedInvalidationListenerBuilder;
import org.codefx.libfx.nesting.listener.NestedInvalidationListenerHandle;

/**
 * A superclass for builders for all kinds of nested functionality. Holds the nesting hierarchy (outer observable and
 * nesting steps) and can build a {@link Nesting} from it.
 * <p>
 * Subclasses must not allow nesting if type parameter {@code O} does not also implement {@link ObservableValue}! (Which
 * wouldn't make sense anyhow because then no value would be available for the nesting step.)
 *
 * @param <T>
 *            the type of the wrapped value
 * @param <O>
 *            the type of {@link Observable} this builder uses as an inner observable
 */
abstract class AbstractNestingBuilderOnObservable<T, O extends Observable> {

	/*
	 * A builder can either be the outer or a nested builder of a nesting. In the first case, 'outerObservable' is
	 * non-null, in the second case 'previousBuilder' and 'nestedGetter' are non-null. The method 'isOuterBuilder()'
	 * indicates which kind of builder this is.
	 */

	//#region PROPERTIES

	/**
	 * The outer observable upon which all nestings depend. This is only non-null for the outer builder (indicated by
	 * {@link #isOuterBuilder()}). All others have a {@link #previousBuilder} and a {@link #nestingStep}.
	 */
	private final O outerObservable;

	/**
	 * The previous builder upon which this builder depends. This is only non-null for nested builders (indicated by
	 * {@link #isOuterBuilder()}).
	 */
	private final AbstractNestingBuilderOnObservable<?, ?> previousBuilder;

	/**
	 * The function which performs the {@link NestingStep} from an instance of the previous builder's wrapped type to
	 * the next observable. This is only non-null for nested builders (indicated by {@link #isOuterBuilder()}).
	 */
	private final NestingStep<?, ? extends O> nestingStep;

	//#end PROPERTIES

	//#region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder, i.e. has the specified {@link #outerObservable} .
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	protected AbstractNestingBuilderOnObservable(O outerObservable) {
		Objects.requireNonNull(outerObservable, "The argument 'outerObservable' must not be null.");

		this.outerObservable = outerObservable;
		this.previousBuilder = null;
		this.nestingStep = null;
	}

	/**
	 * Creates a new nesting builder which acts as a nested builder, i.e. has the specified {@link #previousBuilder} and
	 * {@link #nestingStep}.
	 *
	 * @param <P>
	 *            the type the previous builder wraps
	 * @param previousBuilder
	 *            the previous builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 */
	protected <P> AbstractNestingBuilderOnObservable(
			AbstractNestingBuilderOnObservable<P, ?> previousBuilder, NestingStep<P, ? extends O> nestingStep) {

		Objects.requireNonNull(previousBuilder, "The argument 'previousBuilder' must not be null.");
		Objects.requireNonNull(nestingStep, "The argument 'nestingStep' must not be null.");

		this.outerObservable = null;
		this.previousBuilder = previousBuilder;
		this.nestingStep = nestingStep;
	}

	//#end CONSTRUCTION

	// #region BUILD

	/**
	 * Creates a new nesting from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new instance of {@link Nesting}
	 */
	public Nesting<O> buildNesting() {
		if (isOuterBuilder())
			return new ShallowNesting<>(outerObservable);

		// create a construction kit and use it to create a deep nesting
		NestingConstructionKit kit = createNestingConstructionKit();
		return new DeepNesting<>(kit.getOuterObservable(), kit.getNestingSteps());
	}

	/**
	 * Indicates whether this builder is the outer builder.
	 *
	 * @return true if this is the outer builder
	 */
	private boolean isOuterBuilder() {
		return outerObservable != null;
	}

	/**
	 * Returns a nesting construction kit based in this builder's current settings.
	 *
	 * @return an instance of {@link NestingConstructionKit}
	 */
	private NestingConstructionKit createNestingConstructionKit() {
		NestingConstructionKit kit = new NestingConstructionKit();
		fillNestingConstructionKit(kit);
		return kit;
	}

	/**
	 * Fills the specified kit with an observable value and all nesting steps which were given to this and its previous
	 * nesting builders. The steps' order is from outer to inner property and hence is correct for the constructor of
	 * {@link DeepNesting}.
	 *
	 * @param kit
	 *            the {@link NestingConstructionKit} to fill with an {@link #outerObservable} and {@link #nestingStep
	 *            nestingSteps}
	 */
	@SuppressWarnings("rawtypes")
	private void fillNestingConstructionKit(NestingConstructionKit kit) {

		/*
		 * Uses recursion to move up the chain of 'previousNestedBuilder's until the outer builder is reached. This
		 * builder's 'outerObservable' is set to the specified 'kit'. The 'kit's list of nesting steps is then filled
		 * when the recursion is closed, i.e. top down from the outermost builder to the inner one.
		 */

		if (isOuterBuilder())
			/*
			 * This class' contract states that nesting must not occur when the outerObservable's type O is not also an
			 * 'ObservableValue'.
			 */
			kit.setOuterObservable((ObservableValue) outerObservable);
		else {
			previousBuilder.fillNestingConstructionKit(kit);
			kit.getNestingSteps().add(nestingStep);
		}
	}

	//#end BUILD

	// #region LISTENERS

	/**
	 * Adds the specified invalidation listener to the nesting hierarchy's inner {@link Observable}.
	 *
	 * @param listener
	 *            the added {@link InvalidationListener}
	 * @return the {@link NestedInvalidationListenerHandle} which can be used to check the nesting's state
	 */
	public NestedInvalidationListenerHandle addListener(InvalidationListener listener) {
		Nesting<O> nesting = buildNesting();
		return NestedInvalidationListenerBuilder
				.forNesting(nesting)
				.withListener(listener)
				.buildAttached();
	}

	//#end LISTENERS

	// #region PRIVATE CLASSES

	/**
	 * An editable class which can be used to collect all instances needed to call
	 * {@link DeepNesting#DeepNesting(ObservableValue, List) new Nesting(...)}.
	 */
	@SuppressWarnings("rawtypes")
	protected static class NestingConstructionKit {

		// #region PROPERTIES

		/**
		 * The outer {@link ObservableValue}
		 */
		private ObservableValue outerObservable;

		/**
		 * The list of functions which perform the {@link NestingStep NestingSteps} from one {@link ObservableValue
		 * observable} to the next.
		 */
		private final List<NestingStep> nestingSteps;

		//#end PROPERTIES

		// #region CONSTRUCTOR

		/**
		 * Creates a new empty construction kit.
		 */
		public NestingConstructionKit() {
			nestingSteps = new ArrayList<>();
		}

		//#end CONSTRUCTOR

		// #region ACCESSORS

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
		 * @return the list of {@link Function Functions} which get the nested {@link ObservableValue observables}
		 */
		public List<NestingStep> getNestingSteps() {
			return nestingSteps;
		}

		//#end ACCESSORS

	}

	//#end PRIVATE CLASSES

}
