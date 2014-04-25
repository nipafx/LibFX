package org.codefx.nesting;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * An implementation of {@link Nesting} which uses an outer {@link ObservableValue} and a series of nested value getters
 * to get the {@link #innerProperty()}.
 *
 * @param <O>
 *            the hierarchy's innermost type of {@link ObservableValue}
 */
@SuppressWarnings("rawtypes")
final class DeepNesting<O extends ObservableValue<?>> implements Nesting<O> {

	//#formatter:off

	/*
	 * GENERIC TYPES
	 *
	 * Because the depth of the nesting is variable, the number of involved types is determined at runtime. This class
	 * can hence not use generics for type safety. So it uses tons of raw types, which only works out if the
	 * constructor is called with the correctly typed outer observable and "nesting steps".
	 *
	 *
	 * DATA STRUCTURES
	 *
	 * This nesting uses arrays to store instances which are needed to resolve the nesting. Those arrays all have the
	 * same length ('maxLevel') and a uniform structure: the same indices correspond to the same levels in the nesting
	 * hierarchy.
	 *
	 * observables:	outer nested ... nested inner
	 * level:		  0     1    ...  n-1    n
	 * getters[]:	  x     x     x    x			// each getter uses values[level] to get new observ.[level + 1]
	 * observ.[]:	  x     x     x    x			// stored to remove listeners; [0] only stored for uniform loop
	 * values[]:	  x     x     x    x			// stored to compare values and end loop upon reaching same value
	 * listeners[]:	  x     x     x    x			// stored to remove and add them
	 *
	 *
	 * BEHAVIOR
	 *
	 * TODO: document
	 */

	//#formatter:on

	// #region PROPERTIES

	/**
	 * The level of the nesting, which is also the length of the arrays.
	 */
	private final int maxLevel;

	/**
	 * The getters which implement the nesting step from one observable value to the next.
	 */
	private final Function[] nestedObservableGetters;

	/**
	 * The current hierarchy of observable values.
	 */
	private final ObservableValue[] observables;

	/**
	 * The values currently held by the observable values.
	 */
	private final Object[] values;

	/**
	 * The change listeners which are added to the observable values.
	 */
	private final ChangeListener[] changeListeners;

	/**
	 * The property holding the current innermost observable value.
	 */
	private final Property<O> inner;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Creates a new deep nesting which depends on the specified outer observable value and uses the specified getters
	 * for its "nesting steps".
	 *
	 * @param outerObservableValue
	 *            the {@link ObservableValue} on which this nesting depends
	 * @param nestedObservableGetters
	 *            the {@link Function Functions} which perform the "nesting step" from one observable's value to the
	 *            next observable; the getters must be ordered such that:
	 *            <ul>
	 *            <li>the first accepts an argument of the type wrapped by the {@code outerObservableValue}
	 *            <li>each next accepts an argument of the type wrapped by the observable returned by the previous
	 *            getter
	 *            </ul>
	 *            These conditions are not checked by the compiler nor during construction but will later lead to
	 *            {@link ClassCastException ClassCastExceptions}.
	 */
	public DeepNesting(ObservableValue outerObservableValue, List<Function> nestedObservableGetters) {
		Objects.requireNonNull(outerObservableValue, "The argument 'outerObservableValue' must not be null.");
		Objects.requireNonNull(nestedObservableGetters, "The argument 'nestedObservableGetters' must not be null.");
		if (nestedObservableGetters.size() < 1)
			throw new IllegalArgumentException("The list 'nestedObservableGetters' must have at least length 1.");

		maxLevel = nestedObservableGetters.size();

		this.observables = createObservables(outerObservableValue, maxLevel);
		this.values = new Object[maxLevel];
		this.nestedObservableGetters = nestedObservableGetters.toArray(new Function[maxLevel]);
		this.changeListeners = createChangeListeners(maxLevel);
		this.inner = new SimpleObjectProperty<O>(this, "inner");

		initializeNesting();
	}

	/**
	 * Creates an initialized array of observables. Its first item is the specified outer observable (its other items
	 * are null).
	 *
	 * @param outerObservable
	 *            the outer observable upon which this nesting depends
	 * @param levels
	 *            the number of levels, which is also the new array's length
	 * @return an initialized array of {@link ObservableValue ObservableValues}
	 */
	private static ObservableValue[] createObservables(ObservableValue outerObservable, int levels) {
		ObservableValue[] observables = new ObservableValue[levels];
		observables[0] = outerObservable;
		return observables;
	}

	/**
	 * Creates an array of change listeners.
	 *
	 * @param levels
	 *            the number of levels, which is also the new array's length
	 * @return an array of {@link ChangeListener ChangeListeners}
	 */
	private ChangeListener[] createChangeListeners(int levels) {
		ChangeListener[] listeners = new ChangeListener[levels];
		for (int level = 0; level < levels; level++) {
			final int theLevel = level;
			listeners[level] = (observable, oldValue, newValue) -> updateNestingFromLevel(theLevel);
		}
		return listeners;
	}

	/**
	 * Initializes this nesting by filling the arrays {@link #observables} and {@link #values} and adding the
	 * corresponding {@link #changeListeners changeListener} to each observable.
	 */
	private void initializeNesting() {
		/*
		 * Simply update the nesting from level 0 on. This only works if certain preconditions are met (which depend on
		 * the structure of 'updateNestingFromLevel') so make sure to create them first.
		 */
		initializeNestingLevel0();
		updateNestingFromLevel(0);
	}

	/**
	 * Creates the preconditions necessary to use {@link #updateNestingFromLevel(int) updateNestingFromLevel(0)} to
	 * initialize this nesting.
	 */
	@SuppressWarnings("unchecked")
	private void initializeNestingLevel0() {

		// WARNING:
		// This method is highly coupled to 'updateNestingFromLevel'!
		// Make sure to inspect both methods upon changing one of them.

		// if 'updateNestingFromLevel' encounters the same value in the 'values' array as in the corresponding property,
		// it stops updating so make sure this cannot happen (if the property actually contains null, nothing is left to do)
		values[0] = null;

		// if 'updateNestingFromLevel' encounters the same property in the 'observables' array
		// as on the currently checked level, it does not add a listener so do that here
		observables[0].addListener(changeListeners[0]);
	}

	//#end CONSTRUCTION

	/**
	 * Updates the nesting from the specified level on. This includes moving listeners from old to new observables and
	 * updating the arrays {@link #observables} and {@link #values}.
	 *
	 * @param startLevel
	 *            the level on which to start updating; this will be the one to which the {@link #observables
	 *            observable} which changed its value belongs
	 */
	@SuppressWarnings("unchecked")
	private void updateNestingFromLevel(int startLevel) {

		// WARNING:
		// This method is highly coupled to 'initializeNestingLevel0'!
		// Make sure to inspect both methods upon changing one of them.

		int currentLevel = startLevel;

		ObservableValue currentObservable = observables[startLevel];
		// note that unless the observable has a strange implementation which calls change listeners
		// even though nothing changed, this will always be true
		boolean currentValueChanged = values[currentLevel] != currentObservable.getValue();

		// there is no listener on the inner level's observable so the start level can never be the inner level
		boolean currentIsInnerLevel = false;

		/*
		 * Loop through the levels [startLevel; innerLevel - 1] unless a level is found where the stored value equals
		 * the current one. In that case all higher levels must be identical and nothing more needs to be updated. Note
		 * that the loop will not stop on null observables and null values - instead it continues and still compares to
		 * stored values.
		 */
		while (currentValueChanged && !currentIsInnerLevel) {
			// update 'observables' array and move listener from old to new observable;
			// (note that the test below is never true for the 'startLevel': the listener to that observable called
			// this method because the observable's _value_ changed - hence the observable itself cannot have changed.)
			ObservableValue storedObservable = observables[currentLevel];
			if (storedObservable != currentObservable) {
				observables[currentLevel] = currentObservable;
				if (storedObservable != null)
					storedObservable.removeListener(changeListeners[currentLevel]);
				if (currentObservable != null)
					currentObservable.addListener(changeListeners[currentLevel]);
			}

			// update 'values' array
			Object storedValue = values[currentLevel];
			Object currentValue = null;
			if (currentObservable != null)
				currentValue = currentObservable.getValue();
			currentValueChanged = storedValue != currentValue;
			if (currentValueChanged)
				values[currentLevel] = currentValue;

			// if the value changed, move to next level
			if (currentValueChanged) {
				// get the values for the next level ...
				ObservableValue nextObservable = null;
				if (currentValue != null)
					nextObservable = (ObservableValue) nestedObservableGetters[currentLevel].apply(currentValue);
				boolean nextIsInnerLevel = (currentLevel + 1 == maxLevel);

				// ... assign them ...
				currentObservable = nextObservable;
				currentIsInnerLevel = nextIsInnerLevel;

				// ... and finally increase level counter
				currentLevel++;
			}
		}

		// if the loop encountered a level where the stored and the current value are identical,
		// all higher levels must be identical so nothing is left to do here
		if (!currentValueChanged)
			return;

		// if the inner level was reached, set its property as the new inner property
		if (currentIsInnerLevel)
			inner.setValue((O) currentObservable);
	}

	// #region PROPERTY ACCESS

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadOnlyProperty<O> innerProperty() {
		return inner;
	}

	//#end PROPERTY ACCESS

}
